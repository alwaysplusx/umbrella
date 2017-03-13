package com.harmony.umbrella.log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ServiceLoader;

import org.springframework.util.ClassUtils;

import com.harmony.umbrella.log.Level.StandardLevel;
import com.harmony.umbrella.log.support.CommonLogProvider;
import com.harmony.umbrella.log.support.Slf4jLogProvider;
import com.harmony.umbrella.util.Environments;

/**
 * 
 * @author wuxii@foxmail.com
 */
public final class Logs {

    private static LogProvider logProvider;
    static final boolean LOG_FULL_NAME;
    static final StandardLevel LOG_LEVEL;

    static final boolean slf4jFactoryPresent;
    static final boolean commonLogPresent;

    static {
        LOG_FULL_NAME = Boolean.valueOf(Environments.getProperty("umbrella.log.fullname", "false"));
        LOG_LEVEL = StandardLevel.valueOf(Environments.getProperty("umbrella.log.level", "DEBUG"));
        slf4jFactoryPresent = ClassUtils.isPresent("org.slf4j.LoggerFactory", Logs.class.getClassLoader());
        commonLogPresent = ClassUtils.isPresent("org.apache.commons.logging.Log", Logs.class.getClassLoader());
        flushProvider();
    }

    public static void flushProvider() {
        ServiceLoader<LogProvider> providers = ServiceLoader.load(LogProvider.class);
        for (LogProvider provider : providers) {
            logProvider = provider;
            break;
        }
        if (logProvider == null) {
            logProvider = slf4jFactoryPresent ? new Slf4jLogProvider() : commonLogPresent ? new CommonLogProvider() : new SystemLogProvider();
        }
        StaticLogger.debug("Load log provider {}", logProvider);
    }

    /**
     * 从调用栈中找到上层类名的log
     * 
     * @return log
     */
    public static Log getLog() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        return logProvider.getLogger(sts[2].getClassName());
    }

    /**
     * 通过类名创建对应的log
     * 
     * @param clazz
     *            log的类名
     * @return log
     */
    public static Log getLog(Class<?> clazz) {
        return logProvider.getLogger(clazz.getName());
    }

    /**
     * 通过名称创建对应的log
     * 
     * @param className
     *            log名称
     * @return log
     */
    public static Log getLog(String className) {
        return logProvider.getLogger(className);
    }

    static String fullyQualifiedClassName(Class<?> clazz, int beforeIndex) {
        return fullyQualifiedClassName(clazz.getName(), beforeIndex);
    }

    static String fullyQualifiedClassName(String className, int beforeIndex) {
        StackTraceElement ste = find(className, beforeIndex);
        return ste == null ? null : ste.toString();
    }

    /**
     * 在当前线程中查找对应类的stackTraceElement
     * 
     * @param className
     * @param reversal
     * @return
     */
    static StackTraceElement find(String className, int beforeIndex) {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        for (int i = stackTrace.length - 1; i >= 0; i--) {
            if (stackTrace[i].getClassName().equals(className)) {
                int index = i + beforeIndex;
                if (index > stackTrace.length - 1) {
                    return null;
                }
                return stackTrace[index];
            }
        }
        return null;
    }

    static final class SystemLogProvider implements LogProvider {

        @Override
        public Log getLogger(String className) {
            return new SystemLog(className);
        }

    }

    static final class SystemLog extends AbstractLog {

        private static final OutputStream out = System.out;
        private static final OutputStream err = System.err;

        private String caller;
        private boolean fullName;

        public SystemLog(String className) {
            this(className, LOG_LEVEL, LOG_FULL_NAME);
        }

        SystemLog(String className, StandardLevel level, boolean fullName) {
            super(className);
            this.caller = AbstractLog.class.getName();
            this.fullName = fullName;
        }

        public SystemLog(String className, Object obj) {
            super(className);
            this.caller = (String) obj;
        }

        @Override
        public Log relative(Object relativeProperties) {
            return new SystemLog(caller, relativeProperties);
        }

        @Override
        protected void logMessage(Level level, Message message, Throwable t) {
            print(level, message.getFormattedMessage(), t);
        }

        @Override
        protected void logMessage(Level level, LogInfo logInfo) {
            print(level, logInfo.toString(), logInfo.getThrowable());
        }

        private void print(Level level, String message, Throwable t) {
            OutputStream stream;

            if (level.isMoreSpecificThan(Level.WARN)) {
                stream = err;
            } else {
                stream = out;
            }

            String threadName = Thread.currentThread().getName();
            String levelName = level.getName();

            StringBuilder sb = new StringBuilder();
            sb.append("[").append(levelName.charAt(0)).append("] [").append(threadName).append("] ");
            sb.append(getFullyQualifiedClassName()).append("- ").append(message).append("\n");
            try {
                stream.write(sb.toString().getBytes());
                if (t != null) {
                    t.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String getFullyQualifiedClassName() {
            StackTraceElement ste = Logs.find(caller, 1);
            if (fullName) {
                return ste.toString();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(".").append(ste.getMethodName()).append("(").append(ste.getFileName()).append(":").append(ste.getLineNumber()).append(")");
            return sb.toString();
        }
    }

}

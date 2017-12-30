package com.harmony.umbrella.log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ServiceLoader;

import org.springframework.util.ClassUtils;

import com.harmony.umbrella.log.Level.StandardLevel;
import com.harmony.umbrella.log.spi.CommonsLogProvider;
import com.harmony.umbrella.log.spi.Log4j2LogProvider;
import com.harmony.umbrella.log.spi.Log4jLogProvider;
import com.harmony.umbrella.log.spi.Slf4jLogProvider;
import com.harmony.umbrella.util.Environments;

/**
 * 自动分析当前类路径下的各个日志框架, 起到加载日志框架的作用, 其优先级别为 log4j2 > log4j > slf4j > common. 可以通过加入META-INF/service/
 * 
 * @author wuxii@foxmail.com
 */
public final class Logs {

    private static LogProvider logProvider;
    static final boolean LOG_FULL_NAME;
    static final StandardLevel LOG_LEVEL;

    static final boolean SLF4J_PRESENT;
    static final boolean COMMON_PRESENT;
    static final boolean LOG4J_PRESENT;
    static final boolean LOG4J2_PRESENT;

    static {
        LOG_FULL_NAME = Boolean.valueOf(Environments.getProperty("umbrella.log.fullname", "false"));
        LOG_LEVEL = StandardLevel.valueOf(Environments.getProperty("umbrella.log.level", "ERROR"));

        SLF4J_PRESENT = ClassUtils.isPresent("org.slf4j.LoggerFactory", Logs.class.getClassLoader());
        COMMON_PRESENT = ClassUtils.isPresent("org.apache.commons.logging.Log", Logs.class.getClassLoader());
        LOG4J_PRESENT = ClassUtils.isPresent("org.apache.log4j.LogManager", Logs.class.getClassLoader());
        LOG4J2_PRESENT = ClassUtils.isPresent("org.apache.logging.log4j.LogManager", Logs.class.getClassLoader());
        flushProvider();
    }

    synchronized static void flushProvider() {
        try {
            ServiceLoader<LogProvider> providers = ServiceLoader.load(LogProvider.class);
            for (LogProvider provider : providers) {
                logProvider = provider;
                break;
            }
        } catch (Exception e) {
        }
        if (logProvider == null) {
            if (LOG4J2_PRESENT) {
                logProvider = new Log4j2LogProvider();
            } else if (LOG4J_PRESENT) {
                logProvider = new Log4jLogProvider();
            } else if (SLF4J_PRESENT) {
                logProvider = new Slf4jLogProvider();
            } else if (COMMON_PRESENT) {
                logProvider = new CommonsLogProvider();
            } else {
                logProvider = new SystemLogProvider();
            }
        }
        StaticLogger.debug("Load log provider {}", logProvider);
    }

    public synchronized static void setLogProvider(LogProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("provider must not null");
        }
        Logs.logProvider = provider;
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

    public static final class SystemLog extends AbstractLog {

        private static final OutputStream out = System.out;
        private static final OutputStream err = System.err;

        private String caller;
        private boolean fullName;
        private StandardLevel level;

        SystemLog(String className) {
            this(className, LOG_LEVEL, LOG_FULL_NAME);
        }

        SystemLog(String className, StandardLevel level, boolean fullName) {
            super(className);
            this.caller = AbstractLog.class.getName();
            this.fullName = fullName;
            this.level = level;
        }

        public void setLevel(StandardLevel level) {
            if (level != null) {
                this.level = level;
            }
        }

        @Override
        public boolean isEnabled(Level level) {
            return level == null ? false : this.level.isLessSpecificThan(level.standardLevel);
        }

        @Override
        protected Log relative(Object relativeProperties) {
            SystemLog result = new SystemLog(caller);
            result.caller = this.caller;
            result.fullName = fullName;
            result.level = level;
            return result;
        }

        @Override
        protected void logMessage(Level level, LogInfo logInfo) {
            print(level, logInfo.toString(), logInfo.getThrowable());
        }

        @Override
        protected void logMessage(Level level, Message message, Throwable t) {
            print(level, message.getFormattedMessage(), t);
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

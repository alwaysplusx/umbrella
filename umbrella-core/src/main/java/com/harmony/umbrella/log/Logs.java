/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ServiceLoader;

import com.harmony.umbrella.log.Level.StandardLevel;
import com.harmony.umbrella.util.Environments;

/**
 * 
 * @author wuxii@foxmail.com
 */
public final class Logs {

    private static LogProvider logProvider;
    static final boolean LOG_FULL_NAME;
    static final StandardLevel LOG_LEVEL;

    static {
        
        LOG_FULL_NAME = Boolean.valueOf(Environments.getProperty("umbrella.log.fullname", "false"));
        LOG_LEVEL = StandardLevel.valueOf(Environments.getProperty("umbrella.log.level", "DEBUG"));
        
        flushProvider();
        
    }

    public static void flushProvider() {
        ServiceLoader<LogProvider> providers = ServiceLoader.load(LogProvider.class);
        for (LogProvider provider : providers) {
            logProvider = provider;
            break;
        }
        if (logProvider == null) {
            logProvider = new SystemLogProvider();
        }
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

        private String callerFQCN;
        private boolean fullName;

        public SystemLog(String className) {
            this(className, LOG_LEVEL, LOG_FULL_NAME);
        }

        SystemLog(String className, StandardLevel level, boolean fullName) {
            super(className);
            this.callerFQCN = AbstractLog.class.getName();
            this.fullName = fullName;
            this.setLevel(level);
        }

        public SystemLog(String className, Object obj) {
            super(className);
            this.callerFQCN = (String) obj;
        }

        @Override
        public Log relative(Object relativeProperties) {
            return new SystemLog(callerFQCN, relativeProperties);
        }

        @Override
        protected void logMessage(Level level, Message message, Throwable t) {
            print(level, message.getFormattedMessage(), t);
        }

        @Override
        protected void logMessage(Level level, LogInfo logInfo) {
            print(level, logInfo.toString(), logInfo.getException());
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
            StackTraceElement ste = Logs.find(callerFQCN, 1);
            if (fullName) {
                return ste.toString();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(".").append(ste.getMethodName()).append("(").append(ste.getFileName()).append(":").append(ste.getLineNumber()).append(")");
            return sb.toString();
        }
    }
}

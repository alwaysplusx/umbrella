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

import com.harmony.umbrella.log.spi.LogProvider;
import com.harmony.umbrella.log.support.AbstractLog;
import com.harmony.umbrella.log.util.StackUtils;

/**
 * 
 * @author wuxii@foxmail.com
 */
public class Logs {

    private static LogProvider logProvider;

    static {
        init();
    }

    public static void init() {
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

    static class SystemLogProvider implements LogProvider {

        @Override
        public Log getLogger(String className) {
            return new SystemLog(className);
        }

    }

    static class SystemLog extends AbstractLog {

        private static final OutputStream out = System.out;
        private static final OutputStream err = System.err;

        public SystemLog(String className) {
            super(className);
        }

        public SystemLog(String className, Object obj) {
            super(className);
        }

        @Override
        public Log relative(Object relativeProperties) {
            return this;
        }

        @Override
        protected void logMessage(Level level, Message message, Throwable t) {
            print(level, message.getFormattedMessage());
        }

        @Override
        protected void logMessage(Level level, LogInfo logInfo) {
            print(level, logInfo.toString());
        }

        private void print(Level level, String message) {
            OutputStream stream;

            if (level.isMoreSpecificThan(Level.WARN)) {
                stream = err;
            } else {
                stream = out;
            }

            String threadName = Thread.currentThread().getName();
            String levelName = level.getName();

            StringBuilder sb = new StringBuilder();
            sb.append("[").append(threadName).append("] [").append(levelName).append("] ");
            sb.append(StackUtils.fullyQualifiedClassName(AbstractLog.class.getName(), 1)).append("- ").append(message).append("\n");

            try {
                stream.write(sb.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

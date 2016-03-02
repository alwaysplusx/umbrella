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
package com.harmony.umbrella.log.impl;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.LogProvider;
import com.harmony.umbrella.log.Message;
import com.harmony.umbrella.log.support.AbstractLog;

/**
 * @author wuxii@foxmail.com
 */
public class Log4jLogProvider implements LogProvider {

    @Override
    public Log getLogger(String className) {
        return new Log4jLog(className);
    }

    static class Log4jLog extends AbstractLog {

        public static final String SELF_FQCN = Log4jLog.class.getName();

        public static final String SUPER_FQCN = AbstractLog.class.getName();

        private final String callerFQCN;

        private final Logger logger;

        public Log4jLog(String className) {
            this(className, SUPER_FQCN);
        }

        private Log4jLog(String className, String callerFQCN) {
            super(className);
            this.logger = Logger.getLogger(className);
            this.callerFQCN = callerFQCN;

            this.isTraceEnabled = logger.isTraceEnabled();
            this.isDebugEnabled = logger.isDebugEnabled();
            this.isInfoEnabled = logger.isInfoEnabled();
            this.isWarnEnabled = logger.isEnabledFor(Level.WARN);
            this.isErrorEnabled = logger.isEnabledFor(Level.ERROR);
        }

        @Override
        public Log relative(Object relativeProperties) {
            if (relativeProperties == null) {
                throw new IllegalArgumentException("relative properties is null");
            }
            return callerFQCN.equals(relativeProperties) ? this : new Log4jLog(getName(), (String) relativeProperties);
        }

        @Override
        protected void logMessage(com.harmony.umbrella.log.Level level, Message message, Throwable t) {
            logger.log(callerFQCN, exchange(level), message.getFormattedMessage(), t);
        }

        @Override
        protected void logMessage(com.harmony.umbrella.log.Level level, LogInfo logInfo) {
            logger.log(callerFQCN, exchange(level), logInfo, logInfo.getException());
        }

        private Level exchange(com.harmony.umbrella.log.Level level) {
            switch (level.getStandardLevel()) {
            case ERROR:
                return Level.ERROR;
            case WARN:
                return Level.WARN;
            case INFO:
                return Level.INFO;
            case ALL:
                return Level.ALL;
            case TRACE:
                return Level.TRACE;
            case DEBUG:
                return Level.DEBUG;
            case OFF:
                return Level.OFF;
            }
            return Level.INFO;
        }
    }
}

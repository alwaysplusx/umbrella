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
package com.harmony.umbrella.log.support;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.LogAdapter;

/**
 * @author wuxii@foxmail.com
 */
public class Log4jLogAdapter implements LogAdapter {

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
        protected void log(String message, com.harmony.umbrella.log.Level level, Throwable exception) {
            logger.log(callerFQCN, exchange(level), message, exception);
        }

        private Level exchange(com.harmony.umbrella.log.Level level) {
            if (level == com.harmony.umbrella.log.Level.TRACE) {
                return Level.TRACE;
            }
            if (level == com.harmony.umbrella.log.Level.DEBUG) {
                return Level.DEBUG;
            }
            if (level == com.harmony.umbrella.log.Level.INFO) {
                return Level.INFO;
            }
            if (level == com.harmony.umbrella.log.Level.WARN) {
                return Level.WARN;
            }
            if (level == com.harmony.umbrella.log.Level.ERROR) {
                return Level.ERROR;
            }
            return Level.INFO;
        }

    }
}

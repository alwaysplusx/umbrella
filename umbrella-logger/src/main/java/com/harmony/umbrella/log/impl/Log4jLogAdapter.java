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

        private String callerFQCN;

        private Logger logger;

        public Log4jLog(String className) {
            this(className, SELF_FQCN);
        }

        public Log4jLog(String className, String callerFQCN) {
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
        public void trace(Object msg) {
            logger.log(callerFQCN, Level.TRACE, String.valueOf(msg), null);
        }

        @Override
        public void trace(String format, Object... arguments) {
        }

        @Override
        public void trace(String msg, Throwable t) {
        }

        @Override
        public void debug(Object msg) {
            logger.log(callerFQCN, Level.DEBUG, String.valueOf(msg), null);
        }

        @Override
        public void debug(String format, Object... arguments) {
        }

        @Override
        public void debug(String msg, Throwable t) {
        }

        @Override
        public void info(Object msg) {
            logger.log(callerFQCN, Level.INFO, String.valueOf(msg), null);
        }

        @Override
        public void info(String format, Object... arguments) {
        }

        @Override
        public void info(String msg, Throwable t) {
        }

        @Override
        public void warn(Object msg) {
            logger.log(callerFQCN, Level.WARN, String.valueOf(msg), null);
        }

        @Override
        public void warn(String format, Object... arguments) {
        }

        @Override
        public void warn(String msg, Throwable t) {
        }

        @Override
        public void error(Object msg) {
            logger.log(callerFQCN, Level.ERROR, String.valueOf(msg), null);
        }

        @Override
        public void error(String format, Object... arguments) {
        }

        @Override
        public void error(String msg, Throwable t) {
        }

        @Override
        public Log relative(String callerFQCN) {
            return new Log4jLog(getName(), callerFQCN);
        }

    }
}

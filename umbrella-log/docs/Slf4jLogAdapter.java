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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.LogAdapter;

/**
 * @author wuxii@foxmail.com
 */
public class Slf4jLogAdapter implements LogAdapter {

    @Override
    public Log getLogger(String className) {
        return new Slf4jLog(className);
    }

    static class Slf4jLog extends AbstractLog {

        private Logger logger;

        Slf4jLog(String className) {
            super(className);
            this.logger = LoggerFactory.getLogger(className);
            this.isTraceEnabled = logger.isTraceEnabled();
            this.isDebugEnabled = logger.isDebugEnabled();
            this.isInfoEnabled = logger.isInfoEnabled();
            this.isWarnEnabled = logger.isWarnEnabled();
            this.isErrorEnabled = logger.isErrorEnabled();
        }

        @Override
        public void trace(Object msg) {
            logger.trace("{}", msg);
        }

        @Override
        public void trace(String format, Object... arguments) {
            logger.trace(format, arguments);
        }

        @Override
        public void trace(String msg, Throwable t) {
            logger.trace(msg, t);
        }

        @Override
        public void debug(Object msg) {
            logger.debug("{}", msg);
        }

        @Override
        public void debug(String format, Object... arguments) {
            logger.debug(format, arguments);
        }

        @Override
        public void debug(String msg, Throwable t) {
            logger.debug(msg, t);
        }

        @Override
        public void info(Object msg) {
            logger.info("{}", msg);
        }

        @Override
        public void info(String format, Object... arguments) {
            logger.info(format, arguments);
        }

        @Override
        public void info(String msg, Throwable t) {
            logger.info(msg, t);
        }

        @Override
        public void warn(Object msg) {
            logger.warn("{}", msg);
        }

        @Override
        public void warn(String format, Object... arguments) {
            logger.warn(format, arguments);
        }

        @Override
        public void warn(String msg, Throwable t) {
            logger.warn(msg, t);
        }

        @Override
        public void error(Object msg) {
            logger.error("{}", msg);
        }

        @Override
        public void error(String format, Object... arguments) {
            logger.error(format, arguments);
        }

        @Override
        public void error(String msg, Throwable t) {
            logger.error(msg, t);
        }

    }

}

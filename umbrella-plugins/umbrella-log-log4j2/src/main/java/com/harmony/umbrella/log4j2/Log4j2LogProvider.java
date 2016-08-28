package com.harmony.umbrella.log4j2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.harmony.umbrella.log.AbstractLog;
import com.harmony.umbrella.log.Level;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.LogProvider;
import com.harmony.umbrella.log.Message;
import com.harmony.umbrella.util.Assert;

/**
 * 日志的log4j实现
 * 
 * @author wuxii@foxmail.com
 */
public class Log4j2LogProvider implements LogProvider {

    @Override
    public Log getLogger(String className) {
        return new Log4j2Log(className);
    }

    private final static class Log4j2Log extends AbstractLog {

        private Logger logger;
        private final String caller;

        public Log4j2Log(String name) {
            this(name, FQCN);
        }

        private Log4j2Log(String name, String caller) {
            super(name);
            this.logger = LogManager.getLogger(name);
            this.caller = caller;
            this.isTraceEnabled = logger.isTraceEnabled();
            this.isDebugEnabled = logger.isDebugEnabled();
            this.isInfoEnabled = logger.isInfoEnabled();
            this.isWarnEnabled = logger.isWarnEnabled();
            this.isErrorEnabled = logger.isErrorEnabled();
        }

        @Override
        public Log relative(Object relativeProperties) {
            Assert.notNull(relativeProperties);
            return caller.equals(relativeProperties) ? this : new Log4j2Log(getName(), (String) relativeProperties);
        }

        @Override
        protected void logMessage(Level level, Message message, Throwable t) {
        }

        @Override
        protected void logMessage(Level level, LogInfo logInfo) {
        }

    }
}

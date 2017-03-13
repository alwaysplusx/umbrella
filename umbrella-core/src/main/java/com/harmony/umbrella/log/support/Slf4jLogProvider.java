package com.harmony.umbrella.log.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.log.AbstractLog;
import com.harmony.umbrella.log.Level;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.LogProvider;
import com.harmony.umbrella.log.Message;

/**
 * @author wuxii@foxmail.com
 */
public class Slf4jLogProvider implements LogProvider {

    @Override
    public Log getLogger(String className) {
        return new Slf4jLog(className);
    }

    static final class Slf4jLog extends AbstractLog {

        private Logger logger;

        public Slf4jLog(String className) {
            super(className);
            logger = LoggerFactory.getLogger(className);
        }

        @Override
        public Log relative(Object relativeProperties) {
            return this;
        }

        @Override
        protected void logMessage(Level level, Message message, Throwable t) {
            log(level, message.getFormattedMessage(), t);
        }

        @Override
        protected void logMessage(Level level, LogInfo logInfo) {
            log(level, logInfo.toString(), logInfo.getThrowable());
        }

        private void log(Level level, String message, Throwable t) {
            if (Level.ERROR.equals(level)) {
                logger.error(message, t);
            } else if (Level.WARN.equals(level)) {
                logger.warn(message, t);
            } else if (Level.DEBUG.equals(level)) {
                logger.debug(message, t);
            } else if (Level.TRACE.equals(level)) {
                logger.trace(message, t);
            } else {
                logger.info(message, t);
            }
        }

    }
}

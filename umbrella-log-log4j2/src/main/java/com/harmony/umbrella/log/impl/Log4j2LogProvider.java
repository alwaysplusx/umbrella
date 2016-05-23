package com.harmony.umbrella.log.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.ExtendedLogger;

import com.harmony.umbrella.log.AbstractLog;
import com.harmony.umbrella.log.Level;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.LogProvider;
import com.harmony.umbrella.log.Message;

/**
 * @author wuxii@foxmail.com
 */
public class Log4j2LogProvider implements LogProvider {

    @Override
    public Log getLogger(String className) {
        return new Log4j2Log(className);
    }

    static class Log4j2Log extends AbstractLog {

        public static final String SUPER_FQCN = AbstractLog.class.getName();

        private final Logger logger;
        private final String callerFQCN;

        public Log4j2Log(String className) {
            this(className, SUPER_FQCN);
        }

        private Log4j2Log(String className, String callerFQCN) {
            super(className);
            this.logger = LogManager.getLogger(className);

            this.isTraceEnabled = logger.isTraceEnabled();
            this.isDebugEnabled = logger.isDebugEnabled();
            this.isInfoEnabled = logger.isInfoEnabled();
            this.isWarnEnabled = logger.isWarnEnabled();
            this.isErrorEnabled = logger.isErrorEnabled();

            this.callerFQCN = callerFQCN;
        }

        @Override
        public Log relative(Object relativeProperties) {
            return new Log4j2Log(className, (String) relativeProperties);
        }

        @Override
        protected void logMessage(Level level, Message message, Throwable t) {
            if (logger instanceof ExtendedLogger) {
                ((ExtendedLogger) logger).logIfEnabled(callerFQCN, exchange(level), null, message.getFormattedMessage(), t);
            } else {
                logger.log(exchange(level), message.getFormattedMessage(), t);
            }
        }

        @Override
        protected void logMessage(Level level, LogInfo logInfo) {
            if (logger instanceof ExtendedLogger) {
                ((ExtendedLogger) logger).logIfEnabled(callerFQCN, exchange(level), null, logInfo, logInfo.getThrowable());
            } else {
                logger.info(logInfo);
            }
        }

        private org.apache.logging.log4j.Level exchange(Level level) {
            switch (level.getStandardLevel()) {
            case DEBUG:
                return org.apache.logging.log4j.Level.DEBUG;
            case ERROR:
                return org.apache.logging.log4j.Level.ERROR;
            case INFO:
                return org.apache.logging.log4j.Level.INFO;
            case TRACE:
                return org.apache.logging.log4j.Level.TRACE;
            case WARN:
                return org.apache.logging.log4j.Level.WARN;
            case ALL:
                return org.apache.logging.log4j.Level.ALL;
            case OFF:
                return org.apache.logging.log4j.Level.OFF;
            }
            return org.apache.logging.log4j.Level.INFO;
        }
    }

}

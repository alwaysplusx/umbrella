package com.harmony.umbrella.log.impl;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;

import com.harmony.umbrella.log.AbstractLog;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.LogProvider;
import com.harmony.umbrella.log.Message;

/**
 * @author wuxii@foxmail.com
 */
public class Log4jLogProvider implements LogProvider {

    static {
        LogLog.setQuietMode(true);
    }

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

package com.harmony.umbrella.log.spi;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.harmony.umbrella.log.AbstractLog;
import com.harmony.umbrella.log.Level;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.LogProvider;
import com.harmony.umbrella.log.Message;

/**
 * @author wuxii@foxmail.com
 */
public class Log4jLogProvider implements LogProvider {

    @Override
    public Log getLogger(String className) {
        return new Log4jLogger(className);
    }

    private static final class Log4jLogger extends AbstractLog {

        private Logger logger;
        private String caller;

        public Log4jLogger(String className) {
            this(className, AbstractLog.FQCN);
        }

        public Log4jLogger(String className, String caller) {
            super(className);
            this.logger = LogManager.getLogger(className);
            this.caller = caller;
        }

        @Override
        public boolean isEnabled(Level level) {
            return logger.isEnabledFor(convert(level));
        }

        @Override
        protected void logMessage(Level level, Message message, Throwable t) {
            logger.log(caller, convert(level), message.getFormattedMessage(), t);
        }

        @Override
        protected void logMessage(Level level, LogInfo logInfo) {
            logger.log(caller, convert(level), logInfo, logInfo.getThrowable());
        }

        @Override
        protected Log relative(Object caller) {
            return this.caller.equals(caller) ? this : new Log4jLogger(getName(), (String) caller);
        }

        private org.apache.log4j.Level convert(Level level) {
            if (level != null) {
                switch (level.getStandardLevel()) {
                case ALL:
                    return org.apache.log4j.Level.ALL;
                case DEBUG:
                    return org.apache.log4j.Level.DEBUG;
                case ERROR:
                    return org.apache.log4j.Level.ERROR;
                case INFO:
                    return org.apache.log4j.Level.INFO;
                case OFF:
                    return org.apache.log4j.Level.OFF;
                case TRACE:
                    return org.apache.log4j.Level.TRACE;
                case WARN:
                    return org.apache.log4j.Level.WARN;
                }
            }
            return org.apache.log4j.Level.INFO;
        }
    }

}

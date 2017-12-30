package com.harmony.umbrella.log.spi;

import org.apache.commons.logging.LogFactory;

import com.harmony.umbrella.log.AbstractLog;
import com.harmony.umbrella.log.Level;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.LogProvider;
import com.harmony.umbrella.log.Message;

/**
 * @author wuxii@foxmail.com
 */
public class CommonsLogProvider implements LogProvider {

    @Override
    public Log getLogger(String className) {
        return new CommonsLog(className);
    }

    private static final class CommonsLog extends AbstractLog {

        private org.apache.commons.logging.Log logger;

        public CommonsLog(String className) {
            super(className);
            this.logger = LogFactory.getLog(className);
        }

        @Override
        public boolean isEnabled(Level level) {
            if (level == null) {
                return false;
            }
            switch (level.getStandardLevel()) {
            case ALL:
            case TRACE:
                return logger.isTraceEnabled();
            case DEBUG:
                return logger.isDebugEnabled();
            case INFO:
                return logger.isInfoEnabled();
            case ERROR:
                return logger.isErrorEnabled();
            case WARN:
                return logger.isWarnEnabled();
            default:
                return false;
            }
        }

        @Override
        public Log relative(Object caller) {
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
            if (level != null) {
                switch (level.getStandardLevel()) {
                case ALL:
                case TRACE:
                    logger.trace(message, t);
                    break;
                case DEBUG:
                    logger.debug(message, t);
                    break;
                case INFO:
                    logger.info(message, t);
                    break;
                case ERROR:
                    logger.error(message, t);
                    break;
                case WARN:
                    logger.warn(message, t);
                    break;
                default:
                }
            }
        }

    }

}

package com.harmony.umbrella.log.filter;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.annotation.Logging.LogType;

/**
 * @author wuxii@foxmail.com
 */
public class LogTypeFilter extends Filter {

    private LogType type;

    private boolean allowNullType;

    @Override
    public int decide(LoggingEvent event) {
        Object message = event.getMessage();
        if (message instanceof LogInfo) {
            LogType t = ((LogInfo) message).getType();
            return t == null && allowNullType ? Filter.ACCEPT : type.equals(t) ? Filter.ACCEPT : Filter.DENY;
        }
        return Filter.DENY;
    }

    public void setLogType(String logType) {
        this.type = LogType.valueOf(logType.toUpperCase());
    }

    public void setAllowNullType(boolean allowNullType) {
        this.allowNullType = allowNullType;
    }

}

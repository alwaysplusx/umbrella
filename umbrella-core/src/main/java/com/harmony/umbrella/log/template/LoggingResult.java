package com.harmony.umbrella.log.template;

import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.LogMessage;

/**
 * @author wuxii@foxmail.com
 */
public class LoggingResult {

    Object result;
    Throwable exception;
    LogMessage logMessage;

    public LoggingResult() {
    }

    LoggingResult(Object result, Throwable exception, LogMessage logMessage) {
        this.result = result;
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public Throwable getException() {
        return exception;
    }

    public LogInfo getLogMessage() {
        return logMessage.asInfo();
    }

}

package com.harmony.umbrella.log.template;

import java.io.Serializable;

import com.harmony.umbrella.log.LogInfo;

/**
 * @author wuxii@foxmail.com
 */
public class LoggingResult implements Serializable {

    private static final long serialVersionUID = -3355298069576046273L;

    Object result;
    Throwable exception;
    LogInfo logMessage;

    LoggingResult(Object result, Throwable exception, LogInfo logMessage) {
        this.result = result;
        this.exception = exception;
        this.logMessage = logMessage;
    }

    public Object getResult() {
        return result;
    }

    public Throwable getException() {
        return exception;
    }

    public LogInfo getLogMessage() {
        return logMessage;
    }

}

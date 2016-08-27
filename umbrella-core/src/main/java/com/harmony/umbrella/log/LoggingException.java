package com.harmony.umbrella.log;

/**
 * 自定义日志异常
 * 
 * @author wuxii@foxmail.com
 */
public class LoggingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public LoggingException() {
        super();
    }

    public LoggingException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoggingException(String message) {
        super(message);
    }

    public LoggingException(Throwable cause) {
        super(cause);
    }

}

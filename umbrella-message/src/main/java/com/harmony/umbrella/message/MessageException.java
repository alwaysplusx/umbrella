package com.harmony.umbrella.message;

/**
 * @author wuxii@foxmail.com
 */
public class MessageException extends RuntimeException {

    private static final long serialVersionUID = 2987186182081150566L;

    public MessageException() {
        super();
    }

    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageException(String message) {
        super(message);
    }

    public MessageException(Throwable cause) {
        super(cause);
    }

}

package com.harmony.umbrella.message;

/**
 * @author wuxii@foxmail.com
 */
public class MessageAbortException extends RuntimeException {

    private static final long serialVersionUID = 9084507610028915281L;

    public MessageAbortException() {
        super();
    }

    public MessageAbortException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageAbortException(String message) {
        super(message);
    }

    public MessageAbortException(Throwable cause) {
        super(cause);
    }

}

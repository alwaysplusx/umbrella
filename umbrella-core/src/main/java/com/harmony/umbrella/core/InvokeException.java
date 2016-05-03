package com.harmony.umbrella.core;

/**
 * @author wuxii@foxmail.com
 */
public class InvokeException extends Exception {

    private static final long serialVersionUID = -4338894263088635172L;

    public InvokeException() {
        super();
    }

    public InvokeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvokeException(String message) {
        super(message);
    }

    public InvokeException(Throwable cause) {
        super(cause);
    }

}

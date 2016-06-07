package com.harmony.umbrella.beans;

/**
 * @author wuxii@foxmail.com
 */
public class BeansException extends RuntimeException {

    private static final long serialVersionUID = 6445943761727780839L;

    public BeansException() {
        super();
    }

    public BeansException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeansException(String message) {
        super(message);
    }

    public BeansException(Throwable cause) {
        super(cause);
    }

}

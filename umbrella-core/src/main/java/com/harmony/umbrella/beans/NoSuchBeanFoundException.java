package com.harmony.umbrella.beans;

/**
 * @author wuxii@foxmail.com
 */
public class NoSuchBeanFoundException extends BeansException {

    private static final long serialVersionUID = -6690972685293361663L;

    public NoSuchBeanFoundException() {
        super();
    }

    public NoSuchBeanFoundException(Class<?> beanType) {
        super(String.valueOf(beanType));
    }

    public NoSuchBeanFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchBeanFoundException(String message) {
        super(message);
    }

    public NoSuchBeanFoundException(Throwable cause) {
        super(cause);
    }

}

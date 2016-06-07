package com.harmony.umbrella.beans;

/**
 * @author wuxii@foxmail.com
 */
public class NoUniqueBeanException extends NoSuchBeanFoundException {

    private static final long serialVersionUID = -4646680224736186712L;

    public NoUniqueBeanException() {
        super();
    }

    public NoUniqueBeanException(Class<?> beanType) {
        super(String.valueOf(beanType));
    }

    public NoUniqueBeanException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoUniqueBeanException(String message) {
        super(message);
    }

    public NoUniqueBeanException(Throwable cause) {
        super(cause);
    }

}

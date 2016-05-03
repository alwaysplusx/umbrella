package com.harmony.umbrella.xml;

/**
 * @author wuxii@foxmail.com
 */
public class MappingException extends RuntimeException {

    private static final long serialVersionUID = -3630313104460369222L;

    public MappingException() {
        super();
    }

    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MappingException(String message) {
        super(message);
    }

    public MappingException(Throwable cause) {
        super(cause);
    }

}

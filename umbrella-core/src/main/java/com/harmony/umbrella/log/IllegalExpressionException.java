package com.harmony.umbrella.log;

/**
 * 表达式自定义异常
 * 
 * @author wuxii@foxmail.com
 */
public class IllegalExpressionException extends RuntimeException {

    private static final long serialVersionUID = 1981081998040875764L;

    public IllegalExpressionException() {
        super();
    }

    public IllegalExpressionException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalExpressionException(String message) {
        super(message);
    }

    public IllegalExpressionException(Throwable cause) {
        super(cause);
    }

}

package com.harmony.umbrella.ws;

/**
 * 在执行前 {@linkplain ContextVisitor#visitBefore(Context)}抛出异常
 * 
 * @author wuxii@foxmail.com
 */
public class WebServiceAbortException extends Exception {

    private static final long serialVersionUID = 1L;

    public WebServiceAbortException() {
        super();
    }

    public WebServiceAbortException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebServiceAbortException(String message) {
        super(message);
    }

    public WebServiceAbortException(Throwable cause) {
        super(cause);
    }

}

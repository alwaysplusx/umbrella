package com.harmony.umbrella.web.render;

/**
 * @author wuxii@foxmail.com
 */
public class RenderException extends RuntimeException {

    private static final long serialVersionUID = 2736593222667118138L;

    public RenderException() {
        super();
    }

    public RenderException(String message, Throwable cause) {
        super(message, cause);
    }

    public RenderException(String message) {
        super(message);
    }

    public RenderException(Throwable cause) {
        super(cause);
    }

}

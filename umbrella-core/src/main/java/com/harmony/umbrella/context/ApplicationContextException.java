package com.harmony.umbrella.context;

/**
 * 应用上下文自定义异常
 * 
 * @author wuxii@foxmail.com
 */
public class ApplicationContextException extends RuntimeException {

	private static final long serialVersionUID = -4440235058790777957L;

	public ApplicationContextException() {
		super();
	}

	public ApplicationContextException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApplicationContextException(String message) {
		super(message);
	}

	public ApplicationContextException(Throwable cause) {
		super(cause);
	}

}

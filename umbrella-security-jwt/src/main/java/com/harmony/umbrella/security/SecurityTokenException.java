package com.harmony.umbrella.security;

import org.springframework.security.core.AuthenticationException;

/**
 * @author wuxii
 */
public class SecurityTokenException extends AuthenticationException {

	public SecurityTokenException(String msg, Throwable t) {
		super(msg, t);
	}

	public SecurityTokenException(String msg) {
		super(msg);
	}

}

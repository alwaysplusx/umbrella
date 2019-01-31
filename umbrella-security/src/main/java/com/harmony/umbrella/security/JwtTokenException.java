package com.harmony.umbrella.security;

import org.springframework.security.core.AuthenticationException;

/**
 * @author wuxii
 */
public class JwtTokenException extends AuthenticationException {

    public JwtTokenException(String msg) {
        super(msg);
    }

    public JwtTokenException(String msg, Throwable t) {
        super(msg, t);
    }

}

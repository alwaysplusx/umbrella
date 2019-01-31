package com.harmony.umbrella.security.jwt;

/**
 * @author wuxii
 */
public class JwtDecodeException extends JwtTokenException {

    public JwtDecodeException(String msg) {
        super(msg);
    }

    public JwtDecodeException(String msg, Throwable t) {
        super(msg, t);
    }

}

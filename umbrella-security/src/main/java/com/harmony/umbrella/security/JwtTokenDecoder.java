package com.harmony.umbrella.security;

/**
 * @author wuxii
 */
public interface JwtTokenDecoder {

    JwtToken decode(String tokenValue) throws JwtDecodeException;

}

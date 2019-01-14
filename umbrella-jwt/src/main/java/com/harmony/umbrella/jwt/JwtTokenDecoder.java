package com.harmony.umbrella.jwt;

/**
 * @author wuxii
 */
public interface JwtTokenDecoder {

    JwtToken decode(String tokenValue) throws JwtDecodeException;

}

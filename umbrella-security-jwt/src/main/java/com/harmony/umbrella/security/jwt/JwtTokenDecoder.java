package com.harmony.umbrella.security.jwt;

/**
 * @author wuxii
 */
public interface JwtTokenDecoder {

    JwtToken decode(String tokenValue);

}

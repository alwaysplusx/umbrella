package com.harmony.umbrella.jwt;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wuxii
 */
public interface JwtTokenExtractor {

    String extract(HttpServletRequest request);

}

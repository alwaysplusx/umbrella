package com.harmony.umbrella.security;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wuxii
 */
public interface JwtTokenExtractor {

    String extract(HttpServletRequest request);

}

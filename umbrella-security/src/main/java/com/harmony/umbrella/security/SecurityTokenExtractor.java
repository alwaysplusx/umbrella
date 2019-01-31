package com.harmony.umbrella.security;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wuxii
 */
public interface SecurityTokenExtractor {

	SecurityToken extract(HttpServletRequest request);

}

package com.harmony.umbrella.security.jwt.support;

import com.harmony.umbrella.security.jwt.JwtTokenExtractor;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wuxii
 */
public class HttpHeaderJwtTokenExtractor implements JwtTokenExtractor {

    public static final HttpHeaderJwtTokenExtractor INSTANCE = new HttpHeaderJwtTokenExtractor();

    private HttpHeaderJwtTokenExtractor() {
    }

    @Override
    public String extract(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.toLowerCase().startsWith("bearer ")) {
            return null;
        }
        return header.substring(7);
    }

}

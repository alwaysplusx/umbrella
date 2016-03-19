package com.harmony.umbrella.monitor.matcher;

import javax.servlet.http.HttpServletRequest;

import com.harmony.umbrella.monitor.ResourceMatcher;
import com.harmony.umbrella.util.AntPathMatcher;

/**
 * @author wuxii@foxmail.com
 */
public class HttpRequestMatcher extends AntPathMatcher implements ResourceMatcher<HttpServletRequest> {

    @Override
    public boolean matches(String pattern, HttpServletRequest resource) {
        return super.match(pattern, toUri(resource));
    }

    protected String toUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

}

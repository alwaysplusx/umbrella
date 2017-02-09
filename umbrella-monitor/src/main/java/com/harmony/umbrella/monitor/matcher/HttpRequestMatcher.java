package com.harmony.umbrella.monitor.matcher;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.harmony.umbrella.monitor.ResourceMatcher;

/**
 * @author wuxii@foxmail.com
 */
public class HttpRequestMatcher implements ResourceMatcher<HttpServletRequest> {

    private PathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean matches(String pattern, HttpServletRequest resource) {
        return pathMatcher.match(pattern, toUri(resource));
    }

    protected String toUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

}

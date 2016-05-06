package com.harmony.umbrella.monitor.support;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.harmony.umbrella.monitor.AbstractMonitorFilter;
import com.harmony.umbrella.monitor.ResourceMatcher;
import com.harmony.umbrella.monitor.matcher.HttpRequestMatcher;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractHttpMonitorFilter extends AbstractMonitorFilter<HttpServletRequest> implements Filter {

    protected ResourceMatcher<HttpServletRequest> requestMatcher = new HttpRequestMatcher();

    protected abstract void doInterceptor(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        if (isMonitored(request)) {
            doInterceptor(request, response, chain);
            return;
        }
        chain.doFilter(req, resp);
    }

    @Override
    protected ResourceMatcher<HttpServletRequest> getResourceMatcher() {
        return requestMatcher;
    }
}

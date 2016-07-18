package com.harmony.umbrella.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.harmony.umbrella.context.CurrentContextFilter;
import com.harmony.umbrella.monitor.MonitorFilter;

/**
 * @author wuxii@foxmail.com
 */
public class WebCurrentContextFilter extends CurrentContextFilter {

    private MonitorFilter<String> requestMonitorFilter;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String requestURI = ((HttpServletRequest) request).getRequestURI();
        if (requestMonitorFilter.isMonitored(requestURI)) {
            super.doFilter(request, response, chain);
        } else {
            chain.doFilter(request, response);
        }
    }

    public MonitorFilter<String> getRequestMonitorFilter() {
        return requestMonitorFilter;
    }

    public void setRequestMonitorFilter(MonitorFilter<String> requestMonitorFilter) {
        this.requestMonitorFilter = requestMonitorFilter;
    }

}

package com.harmony.umbrella.monitor.support;

import java.io.IOException;
import java.util.Calendar;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.harmony.umbrella.monitor.HttpAttacker;
import com.harmony.umbrella.monitor.attack.SimpleHttpAttacker;
import com.harmony.umbrella.monitor.graph.DefaultHttpGraph;
import com.harmony.umbrella.util.Exceptions;
import com.harmony.umbrella.util.StringUtils;

/**
 * 基于Http监控的Filter
 * 
 * @author wuxii@foxmail.com
 * @see javax.servlet.Filter
 */
public class HttpMonitorFilter extends AbstractHttpMonitor<FilterChain> implements Filter {

    private HttpAttacker httpAttacker = new SimpleHttpAttacker();

    private boolean raiseError;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String patterns = filterConfig.getInitParameter("monitor-patterns");
        if (patterns != null && StringUtils.isNotBlank(patterns)) {
            StringTokenizer st = new StringTokenizer(patterns, ",|");
            while (st.hasMoreTokens()) {
                patternList.add(st.nextToken().trim());
            }
        } else {
            patternList.add(DEFAULT_PATH_PATTERN);
        }
        this.raiseError = Boolean.valueOf(filterConfig.getInitParameter("raise-error"));
    }

    /**
     * 如果不在监控列表中直接跳过监控， 执行chain
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        try {
            monitor(request, response, chain);
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            if (e instanceof ServletException) {
                throw (ServletException) e;
            }
            if (raiseError) {
                throw Exceptions.unchecked(e);
            }
        }
    }

    @Override
    protected Object process(HttpServletRequest request, HttpServletResponse response, FilterChain nexus) throws IOException, ServletException {
        nexus.doFilter(request, response);
        return null;
    }

    @Override
    protected Object aroundMonitor(String resourceId, HttpServletRequest request, HttpServletResponse response, FilterChain nexus) throws Exception {
        Object result = null;
        DefaultHttpGraph graph = new DefaultHttpGraph(resourceId);
        try {
            applyHttpRequestFeature(graph, request);
            // do filter
            result = process(request, response, nexus);
            //
            graph.setResponseTime(Calendar.getInstance());
            applyHttpResponseFeature(graph, response);
        } catch (Exception e) {
            graph.setException(e);
            throw e;
        } finally {
            notifyGraphListeners(graph);
        }
        return result;
    }

    @Override
    protected HttpAttacker getHttpAttacker() {
        return httpAttacker;
    }

}

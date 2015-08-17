/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.monitor.support;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.monitor.AbstractMonitor;
import com.harmony.umbrella.monitor.GraphAnalyzer;
import com.harmony.umbrella.monitor.HttpMonitor;
import com.harmony.umbrella.monitor.ResourceMatcher;
import com.harmony.umbrella.monitor.graph.DefaultHttpGraph;
import com.harmony.umbrella.monitor.matcher.UrlPathMatcher;
import com.harmony.umbrella.monitor.util.MonitorUtils;
import com.harmony.umbrella.util.Exceptions;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * 基于Http监控的Filter
 * 
 * @author wuxii@foxmail.com
 * @see javax.servlet.Filter
 */
public class HttpMonitorFilter extends AbstractMonitor<String> implements HttpMonitor {

    public static final String ANALYZER_CLASS = "analyzer-class";

    private static final Logger log = LoggerFactory.getLogger(HttpMonitorFilter.class);

    private ResourceMatcher<String> resourceMatcher = new UrlPathMatcher();

    /**
     * 在{@linkplain #init(FilterConfig)}时候通过配置形式创建
     */
    private GraphAnalyzer<HttpGraph> analyzer;

    @SuppressWarnings("unchecked")
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String analyzerClassName = filterConfig.getInitParameter(ANALYZER_CLASS);
        if (StringUtils.isBlank(analyzerClassName)) {
            throw new ServletException("please config analyzer-class in HttpMonitorFilter");
        }
        try {
            analyzer = (GraphAnalyzer<HttpGraph>) ReflectionUtils.instantiateClass(analyzerClassName);
        } catch (Exception e) {
            log.error("error analyzer class {}", analyzerClassName, e);
            throw new ServletException(e);
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String resource = MonitorUtils.requestId(request);
        if (!isMonitored(resource)) {
            chain.doFilter(request, response);
            return;
        }
        DefaultHttpGraph graph = new DefaultHttpGraph(resource);
        try {
            MonitorUtils.applyRequest(graph, request);
            graph.setRequestTime(Calendar.getInstance());
            // do filter
            chain.doFilter(request, response);
            //
            graph.setResponseTime(Calendar.getInstance());
            MonitorUtils.applyResponse(graph, request, response);
        } catch (Exception e) {
            graph.setException(e);
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            if (e instanceof ServletException) {
                throw (ServletException) e;
            }
            throw Exceptions.unchecked(e);
        } finally {
            analyzer.analyze(graph);
        }
    }

    @Override
    protected ResourceMatcher<String> getMatcher() {
        return resourceMatcher;
    }

    public GraphAnalyzer<HttpGraph> getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(GraphAnalyzer<HttpGraph> analyzer) {
        this.analyzer = analyzer;
    }

    @Override
    public void destroy() {
        this.cleanAll();
        analyzer = null;
    }

}

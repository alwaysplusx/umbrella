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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.monitor.AbstractHttpMonitor;
import com.harmony.umbrella.monitor.Attacker;
import com.harmony.umbrella.monitor.HttpAttacker;
import com.harmony.umbrella.monitor.graph.DefaultHttpGraph;
import com.harmony.umbrella.util.Exceptions;

/**
 * 基于Http监控的Filter
 * 
 * @author wuxii@foxmail.com
 * @see javax.servlet.Filter
 */
public class HttpMonitorFilter extends AbstractHttpMonitor<FilterChain> implements Filter {

    protected static final Logger LOG = LoggerFactory.getLogger(HttpMonitorFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
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
            this.applyHttpRequestFeature(graph, request);
            // do filter
            result = process(request, response, nexus);
            //
            this.applyHttpResponseFeature(graph, response);
            graph.setResponseTime(Calendar.getInstance());
        } catch (Exception e) {
            graph.setException(e);
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            if (e instanceof ServletException) {
                throw (ServletException) e;
            }
            throw Exceptions.unchecked(e);
        }
        return result;
    }

    @Override
    protected HttpAttacker getHttpAttacker() {
        return null;
    }

    @Override
    protected Attacker<?> getAttacker(Class<? extends Attacker<?>> attackerClass) {
        return null;
    }

}

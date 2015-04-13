/*
 * Copyright 2013-2015 wuxii@foxmail.com.
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
package com.harmony.umbrella.monitor;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.harmony.umbrella.io.utils.AntPathMatcher;
import com.harmony.umbrella.io.utils.PathMatcher;
import com.harmony.umbrella.monitor.util.MonitorUtils;
import com.harmony.umbrella.utils.Exceptions;

/**
 * Http监视抽象类
 * 
 * @author wuxii@foxmail.com
 */
public abstract class AbstractHttpMonitor implements HttpMonitor {

    /**
     * 受到监控的模版名单
     */
    private Map<String, Object> patternList = new ConcurrentHashMap<String, Object>();

    /**
     * 资源名单，综合{@link #getPolicy()}定性资源名单的意义
     */
    private Map<String, Object> resourceList = new ConcurrentHashMap<String, Object>();

    /**
     * 监控策略
     */
    protected MonitorPolicy policy = MonitorPolicy.BlockList;
    private PathMatcher pathMatcher = new AntPathMatcher();
    protected final static Object DEFAULT_PATH_VALUE = new Object();

    public AbstractHttpMonitor() {
        patternList.put(DEFAULT_PATH_PATTERN, DEFAULT_PATH_VALUE);
    }

    /**
     * 保存http监视结果
     * 
     * @param graph
     */
    protected abstract void persistGraph(HttpGraph graph);

    @Override
    public boolean isMonitored(String resource) {
        switch (policy) {
        case Skip:
            return false;
        case All:
            return true;
        case BlockList:
            for (String pattern : patternList.keySet()) {
                if (pathMatcher.match(pattern, resource) && resourceList.containsKey(resource)) {
                    return true;
                }
            }
            return false;
        case WhiteList:
            for (String pattern : patternList.keySet()) {
                if (pathMatcher.match(pattern, resource) && !resourceList.containsKey(resource)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public Set<String> getPatterns() {
        return Collections.unmodifiableSet(patternList.keySet());
    }

    @Override
    public void excludePattern(String pattern) {
        patternList.remove(pattern);
    }

    @Override
    public void includePattern(String pattern) {
        patternList.put(pattern, DEFAULT_PATH_VALUE);
    }

    @Override
    public void includeResource(String resource) {
        resourceList.put(resource, DEFAULT_PATH_VALUE);
    }

    @Override
    public void excludeResource(String resource) {
        resourceList.remove(resource);
    }

    @Override
    public Set<String> getResources() {
        return Collections.unmodifiableSet(resourceList.keySet());
    }

    @Override
    public MonitorPolicy getPolicy() {
        return policy;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String resource = MonitorUtils.requestIdentifie(request);
        if (isMonitored(resource)) {
            DefaultHttpGraph graph = new DefaultHttpGraph(resource);
            graph.setRequestArguments(request);
            try {
                chain.doFilter(request, response);
                graph.setResponseResult(request, response);
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
                graph.setResponseTime(Calendar.getInstance());
                persistGraph(graph);
            }
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    public void setPolicy(MonitorPolicy policy) {
        if (policy == null)
            throw new IllegalArgumentException("policy must not be null");
        this.policy = policy;
    }

    public PathMatcher getPathMatcher() {
        return pathMatcher;
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

}

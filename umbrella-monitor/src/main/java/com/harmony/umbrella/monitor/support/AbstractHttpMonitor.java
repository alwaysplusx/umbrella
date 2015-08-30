/*
 * Copyright 2002-2015 the original author or authors.
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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.monitor.AbstractMonitor;
import com.harmony.umbrella.monitor.HttpAttacker;
import com.harmony.umbrella.monitor.HttpGraph;
import com.harmony.umbrella.monitor.HttpMonitor;
import com.harmony.umbrella.monitor.ResourceMatcher;
import com.harmony.umbrella.monitor.annotation.HttpProperty;
import com.harmony.umbrella.monitor.annotation.HttpProperty.Scope;
import com.harmony.umbrella.monitor.annotation.Mode;
import com.harmony.umbrella.monitor.graph.AbstractGraph;
import com.harmony.umbrella.monitor.graph.DefaultHttpGraph;
import com.harmony.umbrella.monitor.graph.HybridGraph;
import com.harmony.umbrella.monitor.matcher.UrlPathMatcher;
import com.harmony.umbrella.monitor.util.MonitorUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractHttpMonitor<N> extends AbstractMonitor<String> implements HttpMonitor {

    protected static final Logger LOG = LoggerFactory.getLogger(AbstractHttpMonitor.class);

    private ResourceMatcher<String> resourceMatcher;

    /**
     * 监控的入口(拦截器的入口)
     */
    protected Object monitor(HttpServletRequest request, HttpServletResponse response, N nexus) throws Exception {
        String resourceId = getRequestId(request);
        return preMonitor(resourceId) ? aroundMonitor(resourceId, request, response, nexus) : process(request, response, nexus);
    }

    protected boolean preMonitor(String resourceId) {
        return isMonitored(resourceId);
    }

    /**
     * 环绕chain执行，对chain执行监控
     * 
     * @param resourceId
     *            资源唯一键
     * @param req
     *            http请求
     * @param resp
     *            http应答
     * @param chain
     *            FilterChain
     * @throws Exception
     */
    protected abstract Object aroundMonitor(String resourceId, HttpServletRequest request, HttpServletResponse response, N nexus) throws Exception;

    /**
     * 连接执行过程
     * 
     * @param request
     *            http请求
     * @param response
     *            http应答
     * @param nexus
     *            连接
     */
    protected abstract Object process(HttpServletRequest request, HttpServletResponse response, N nexus) throws Exception;

    /**
     * 获取http内部信息工具类
     */
    protected abstract HttpAttacker getHttpAttacker();

    @Override
    protected ResourceMatcher<String> getResourceMatcher() {
        if (resourceMatcher == null) {
            resourceMatcher = new UrlPathMatcher();
        }
        return resourceMatcher;
    }

    public void setResourceMatcher(ResourceMatcher<String> resourceMatcher) {
        this.resourceMatcher = resourceMatcher;
    }

    protected void applyHttpRequestProperty(AbstractGraph graph, HttpServletRequest request, Method method) {
        Map<String, Object> property = attackHttpProperty(request, method, Mode.IN);
        if (property != null && !property.isEmpty()) {
            graph.putArgument(HttpGraph.HTTP_PROPERTY, property);
        }
    }

    protected void applyHttpResponseProperty(AbstractGraph graph, HttpServletRequest request, Method method) {
        Map<String, Object> property = attackHttpProperty(request, method, Mode.OUT);
        if (property != null && !property.isEmpty()) {
            graph.putArgument(HttpGraph.HTTP_PROPERTY, property);
        }
    }

    /**
     * 通过方法上的注解获取request中的属性
     * 
     * @param request
     *            http request
     */
    public Map<String, Object> attackHttpProperty(HttpServletRequest request, Method method, Mode mode) {
        Map<String, Object> result = new HashMap<String, Object>();
        HttpProperty[] properties = getMonitorProperty(method, HttpProperty.class);
        if (properties != null && properties.length > 0) {
            for (HttpProperty hp : properties) {
                Scope scope = hp.scope();
                if (!result.containsKey(scope.name()) && mode.inRange(hp.mode())) {
                    Map<String, Object> property = getHttpAttacker().attack(request, scope, hp.properties());
                    result.put(scope.name(), property);
                }
            }
        }
        return result;
    }

    /**
     * 设置http请求属性, http method, remote address, local address
     * 
     * @param graph
     *            graph
     * @param request
     *            http request
     */
    protected void applyHttpRequestFeature(HybridGraph graph, HttpServletRequest request) {
        graph.setHttpMethod(request.getMethod());
        graph.setRemoteAddr(request.getRemoteAddr());
        graph.setLocalAddr(request.getLocalAddr());
        graph.setQueryString(request.getQueryString());
    }

    protected void applyHttpRequestFeature(DefaultHttpGraph graph, HttpServletRequest request) {
        graph.setHttpMethod(request.getMethod());
        graph.setRemoteAddr(request.getRemoteAddr());
        graph.setLocalAddr(request.getLocalAddr());
        graph.setQueryString(request.getQueryString());
    }

    protected void applyHttpResponseFeature(HybridGraph graph, HttpServletResponse response) {
        graph.setStatus(response.getStatus());
    }

    protected void applyHttpResponseFeature(DefaultHttpGraph graph, HttpServletResponse response) {
        graph.setStatus(response.getStatus());
    }

    protected String getRequestId(HttpServletRequest request) {
        return MonitorUtils.requestId(request);
    }

    @Override
    public void destroy() {
        this.cleanAll();
    }

}

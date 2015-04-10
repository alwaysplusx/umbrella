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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.harmony.umbrella.monitor.util.MonitorUtils;
import com.harmony.umbrella.utils.Exceptions;

/**
 * Http监视抽象类
 * 
 * @author wuxii@foxmail.com
 */
public abstract class AbstractHttpMonitor implements HttpMonitor {

    /**
     * 受到监视的资源
     */
    private Map<String, Object> monitorList = new HashMap<String, Object>();
    /**
     * 是否开启白名单策略，开启后只拦截在监视名单中的资源
     */
    private boolean useWhiteList;

    /**
     * 保存http监视结果
     * 
     * @param graph
     */
    protected abstract void persistGraph(HttpGraph graph);

    @Override
    public void exclude(String resource) {
        monitorList.remove(resource);
    }

    @Override
    public void include(String resource) {
        monitorList.put(resource, null);
    }

    @Override
    public boolean isMonitored(String resource) {
        if (useWhiteList) {
            return monitorList.containsKey(resource);
        }
        return true;
    }

    @Override
    public boolean isUseWhiteList() {
        return useWhiteList;
    }

    @Override
    public void useWhiteList(boolean use) {
        this.useWhiteList = use;
    }

    @Override
    public String[] getMonitorList() {
        Set<String> set = monitorList.keySet();
        return set.toArray(new String[set.size()]);
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

}

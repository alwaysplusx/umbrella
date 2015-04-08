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
package com.harmony.modules.monitor;

import static com.harmony.modules.utils.ObjectUtils.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.Enumeration;
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
import javax.servlet.http.HttpSession;

import com.harmony.modules.monitor.util.MonitorUtils;
import com.harmony.modules.utils.Exceptions;

/**
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
            HttpGraphImpl graph = new HttpGraphImpl(resource);
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

    /**
     * 保存http监视结果
     * @param graph
     */
    protected abstract void persistGraph(HttpGraph graph);

    protected class HttpGraphImpl extends AbstractGraph implements HttpGraph {

        private String method;
        private String remoteAddr;
        private String localAddr;
        private String queryString;
        private int status;

        public HttpGraphImpl(String resource) {
            this.identifie = resource;
            this.result = new HashMap<String, Object>();
        }

        @Override
        public String getMethod() {
            return method;
        }

        @Override
        public String getRemoteAddr() {
            return remoteAddr;
        }

        @Override
        public String getLocalAddr() {
            return localAddr;
        }

        @Override
        public String getQueryString() {
            return queryString;
        }

        @Override
        public int getStatus() {
            return status;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public void setRemoteAddr(String remoteAddr) {
            this.remoteAddr = remoteAddr;
        }

        public void setLocalAddr(String localAddr) {
            this.localAddr = localAddr;
        }

        public void setQueryString(String queryString) {
            this.queryString = queryString;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        @Override
        @Deprecated
        public void setArguments(Map<String, Object> arguments) {
            super.setArguments(arguments);
        }
        
        @Override
        @Deprecated
        public void setResult(Object result) {
            super.setResult(result);
        }
        
        public void setRequestArguments(HttpServletRequest request) {
            this.remoteAddr = request.getRemoteAddr();
            this.localAddr = request.getLocalAddr();
            this.method = request.getMethod();
            this.queryString = request.getQueryString();
            Map<String, Object> sessionAttrMap = new HashMap<String, Object>();
            Map<String, Object> reqAttrMap = new HashMap<String, Object>();
            arguments.put("parameter", request.getParameterMap());
            arguments.put("sessionAttribute", sessionAttrMap);
            arguments.put("requestAttribute", reqAttrMap);
            for (Enumeration<String> names = request.getAttributeNames(); names.hasMoreElements();) {
                String name = names.nextElement();
                reqAttrMap.put(name, request.getAttribute(name));
            }
            HttpSession session = request.getSession();
            for (Enumeration<String> names = session.getAttributeNames(); names.hasMoreElements();) {
                String name = names.nextElement();
                sessionAttrMap.put(name, session.getAttribute(name));
            }
        }

        @SuppressWarnings("unchecked")
        public void setResponseResult(HttpServletRequest request, HttpServletResponse response) {
            this.status = response.getStatus();
            Map<String, Object> sessionAttrMap = new HashMap<String, Object>();
            Map<String, Object> reqAttrMap = new HashMap<String, Object>();
            ((Map<String, Object>) result).put("sessionAttribute", sessionAttrMap);
            ((Map<String, Object>) result).put("requestAttribute", reqAttrMap);
            // 请求时候request中的attribute
            Map<String, Object> requestAttrMap = (Map<String, Object>) arguments.get("requestAttribute");
            for (Enumeration<String> names = request.getAttributeNames(); names.hasMoreElements();) {
                String name = names.nextElement();
                if (requestAttrMap.containsKey(name) && nullSafeEquals(request.getAttribute(name), requestAttrMap.get(name))) {
                    continue;
                }
                reqAttrMap.put(name, request.getAttribute(name));
            }
            // 请求时候的session中的attribute
            Map<String, Object> requestSessionAttrMap = (Map<String, Object>) arguments.get("sessionAttribute");
            HttpSession session = request.getSession();
            for (Enumeration<String> names = session.getAttributeNames(); names.hasMoreElements();) {
                String name = names.nextElement();
                if (requestSessionAttrMap.containsKey(name) && nullSafeEquals(session.getAttribute(name), requestSessionAttrMap.get(name))) {
                    continue;
                }
                sessionAttrMap.put(name, session.getAttribute(name));
            }
        }

    }

}

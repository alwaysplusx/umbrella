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
package com.harmony.umbrella.monitor.graph;

import java.util.HashMap;
import java.util.Map;

import com.harmony.umbrella.monitor.HttpMonitor.HttpGraph;

/**
 * @author wuxii@foxmail.com
 */
public class DefaultHttpGraph extends AbstractGraph<Map<String, Object>> implements HttpGraph {

    protected String method;
    protected String remoteAddr;
    protected String localAddr;
    protected String queryString;
    protected int status;
    protected Map<String, Object> requestParam = new HashMap<String, Object>();

    public DefaultHttpGraph(String resource) {
        super(resource);
        this.responseResult = new HashMap<String, Object>();
    }

    @Override
    public Map<String, Object> getRequestParam() {
        return requestParam;
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

    /*
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
        }*/

}

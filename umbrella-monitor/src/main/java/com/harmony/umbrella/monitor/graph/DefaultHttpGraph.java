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

import static com.harmony.umbrella.util.ObjectUtils.*;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.harmony.umbrella.monitor.HttpMonitor.HttpGraph;

/**
 * 基于http请求监控的结果视图
 * 
 * @author wuxii@foxmail.com
 */
public class DefaultHttpGraph extends AbstractGraph implements HttpGraph {

    protected String method;
    protected String remoteAddr;
    protected String localAddr;
    protected String queryString;
    protected int status;

    public DefaultHttpGraph(String resource) {
        super(resource);
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @Deprecated
    public void setResult(Object result) {
        Map<String, Object> temp = (Map<String, Object>) (this.result);
        if (result == null)
            return;
        if (result instanceof Map) {
            temp.putAll((Map) result);
        } else {
            temp.put(result.toString(), result);
        }
    }

    public void setRequest(HttpServletRequest request, HttpServletResponse response) {
        this.remoteAddr = request.getRemoteAddr();
        this.localAddr = request.getLocalAddr();
        this.method = request.getMethod();
        this.queryString = request.getQueryString();

        // request Attribute
        Map<String, Object> reqAttrMap = new HashMap<String, Object>();
        for (Enumeration<String> names = request.getAttributeNames(); names.hasMoreElements();) {
            String name = names.nextElement();
            reqAttrMap.put(name, request.getAttribute(name));
        }

        // session Attribute
        Map<String, Object> sessionAttrMap = new HashMap<String, Object>();
        HttpSession session = request.getSession();
        for (Enumeration<String> names = session.getAttributeNames(); names.hasMoreElements();) {
            String name = names.nextElement();
            sessionAttrMap.put(name, session.getAttribute(name));
        }

        // set parameter
        arguments.put("parameter", request.getParameterMap());
        arguments.put("requestAttribute", reqAttrMap);
        arguments.put("sessionAttribute", sessionAttrMap);

    }

    @SuppressWarnings("unchecked")
    public void setResponse(HttpServletRequest request, HttpServletResponse response) {
        this.status = response.getStatus();
        // http graph result is a map
        Map<String, Object> result = (Map<String, Object>) this.result;

        // 请求时候request中的attribute
        Map<String, Object> requestAttrMap = (Map<String, Object>) arguments.get("requestAttribute");

        Map<String, Object> responseAttrMap = new HashMap<String, Object>();
        for (Enumeration<String> names = request.getAttributeNames(); names.hasMoreElements();) {
            String name = names.nextElement();
            if (requestAttrMap != null && requestAttrMap.containsKey(name) && nullSafeEquals(request.getAttribute(name), requestAttrMap.get(name))) {
                continue;
            }
            responseAttrMap.put(name, request.getAttribute(name));
        }

        // 请求时候的session中的attribute
        Map<String, Object> requestSessionAttrMap = (Map<String, Object>) arguments.get("sessionAttribute");

        Map<String, Object> responseSessionAttrMap = new HashMap<String, Object>();
        HttpSession session = request.getSession();
        for (Enumeration<String> names = session.getAttributeNames(); names.hasMoreElements();) {
            String name = names.nextElement();
            if (requestSessionAttrMap != null && requestSessionAttrMap.containsKey(name)
                    && nullSafeEquals(session.getAttribute(name), requestSessionAttrMap.get(name))) {
                continue;
            }
            responseSessionAttrMap.put(name, session.getAttribute(name));
        }

        result.put("requestAttribute", responseAttrMap);
        result.put("sessionAttribute", responseSessionAttrMap);

    }

}

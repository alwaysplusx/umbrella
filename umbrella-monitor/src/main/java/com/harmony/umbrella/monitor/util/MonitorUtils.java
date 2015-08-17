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
package com.harmony.umbrella.monitor.util;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.harmony.umbrella.monitor.graph.DefaultHttpGraph;
import com.harmony.umbrella.util.StringUtils;

/**
 * 监听工具类
 * 
 * @author wuxii@foxmail.com
 */
public abstract class MonitorUtils {

    /**
     * 将方转化为唯一的资源限定标识
     * 
     * @param method
     * @return
     */
    public static String methodId(Method method) {
        return method == null ? "" : StringUtils.getMethodId(method);
    }

    /**
     * 将request转为唯一的资源限定标识
     * 
     * @param request
     * @return
     */
    public static String requestId(HttpServletRequest request) {
        return request == null ? null : request.getRequestURI();
    }

    public static void applyRequest(DefaultHttpGraph httpGraph, HttpServletRequest request) {
        httpGraph.setHttpMethod(request.getMethod());
        httpGraph.setRemoteAddr(request.getRemoteAddr());
        httpGraph.setLocalAddr(request.getLocalAddr());
        httpGraph.setQueryString(request.getQueryString());

        // apply parameter
        httpGraph.getArguments().put("parameter", request.getParameterMap());

        // apply attribute
        Map<String, Object> reqAttrMap = new HashMap<String, Object>();
        for (Enumeration<String> names = request.getAttributeNames(); names.hasMoreElements();) {
            String name = names.nextElement();
            reqAttrMap.put(name, request.getAttribute(name));
        }
        httpGraph.getArguments().put("requestAttribute", reqAttrMap);

        // apply session
        Map<String, Object> sessionAttrMap = new HashMap<String, Object>();
        HttpSession session = request.getSession();
        for (Enumeration<String> names = session.getAttributeNames(); names.hasMoreElements();) {
            String name = names.nextElement();
            sessionAttrMap.put(name, session.getAttribute(name));
        }
        httpGraph.getArguments().put("sessionAttribute", sessionAttrMap);

    }

    public static void applyResponse(DefaultHttpGraph httpGraph, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> arguments = httpGraph.getArguments();
        // apply parameter
        arguments.put("parameter", request.getParameterMap());

        // apply attribute
        Map<String, Object> reqAttrMap = new HashMap<String, Object>();
        for (Enumeration<String> names = request.getAttributeNames(); names.hasMoreElements();) {
            String name = names.nextElement();
            reqAttrMap.put(name, request.getAttribute(name));
        }
        arguments.put("requestAttribute", reqAttrMap);

        // apply session
        Map<String, Object> sessionAttrMap = new HashMap<String, Object>();
        HttpSession session = request.getSession();
        for (Enumeration<String> names = session.getAttributeNames(); names.hasMoreElements();) {
            String name = names.nextElement();
            sessionAttrMap.put(name, session.getAttribute(name));
        }
        arguments.put("sessionAttribute", sessionAttrMap);
    }

}

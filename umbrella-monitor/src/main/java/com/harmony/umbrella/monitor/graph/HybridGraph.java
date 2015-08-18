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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.harmony.umbrella.monitor.HttpMonitor.HttpGraph;
import com.harmony.umbrella.monitor.MethodMonitor.MethodGraph;
import com.harmony.umbrella.monitor.util.MonitorUtils;

/**
 * 混合视图, 既包括了Http部分信息, 也包括了方法拦截的信息
 * <p>
 * 适用场景如:Struts2 的拦截器, 既可以包括Http部分信息, 也可以包括拦截的方法信息
 * 
 * @author wuxii@foxmail.com
 */
public class HybridGraph extends AbstractGraph implements HttpGraph, MethodGraph {

    public static final String METHOD_RESULT = HybridGraph.class.getName() + ".METHOD_RESULT";
    public static final String METHOD_ARGUMENT = HybridGraph.class.getName() + ".METHOD_ARGUMENT";

    private final Map<String, Object> result = new HashMap<String, Object>();

    protected Method method;
    protected Object target;

    protected String httpMethod;
    protected String remoteAddr;
    protected String localAddr;
    protected String queryString;
    protected int status;

    public HybridGraph(Method method) {
        this(MonitorUtils.methodId(method));
    }

    public HybridGraph(String identifier) {
        super(identifier);
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Map<String, Object> getResult() {
        return result;
    }

    @Override
    public String getHttpMethod() {
        return httpMethod;
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

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
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

    public void addAllResult(Map<String, Object> result) {
        if (result != null && !result.isEmpty()) {
            this.result.putAll(result);
        }
    }

    public void addAllArgument(Map<String, Object> argument) {
        if (argument != null && !argument.isEmpty()) {
            this.arguments.putAll(argument);
        }
    }

    public Object getMethodResult() {
        return result.get(METHOD_RESULT);
    }

    public void setMethodResult(Object result) {
        this.result.put(METHOD_RESULT, result);
    }

    public Object[] getMethodArguments() {
        return (Object[]) this.arguments.get(METHOD_ARGUMENT);
    }

    public void setMethodArguments(Object[] arguments) {
        this.arguments.put(METHOD_ARGUMENT, arguments);
    }

    public boolean hasMethodGraph() {
        return method != null;
    }

    public boolean hasHttpGraph() {
        return httpMethod != null;
    }

}

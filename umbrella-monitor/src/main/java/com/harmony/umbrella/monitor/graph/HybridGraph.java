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

import com.harmony.umbrella.monitor.MethodMonitor.MethodGraph;
import com.harmony.umbrella.monitor.util.MonitorUtils;

/**
 * 混合视图, 既包括了Http部分信息, 也包括了方法拦截的信息
 * <p>
 * 适用场景如:Struts2 的拦截器, 既可以包括Http部分信息, 也可以包括拦截的方法信息
 * 
 * @author wuxii@foxmail.com
 */
public class HybridGraph extends DefaultHttpGraph implements MethodGraph {

    /**
     * 拦截方法的返回值
     */
    public static final String METHOD_RESULT = HybridGraph.class.getName() + ".METHOD_RESULT";
    /**
     * 拦截方法的请求参数
     */
    public static final String METHOD_ARGUMENT = HybridGraph.class.getName() + ".METHOD_ARGUMENT";

    protected Method method;
    protected Object target;

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

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public boolean hasMethodGraph() {
        return method != null;
    }

    public boolean hasHttpGraph() {
        return httpMethod != null;
    }

    @Override
    public String getDescription() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("{\n")//
                .append("  url:").append(identifier).append("\n");//
        if (method != null) {
            buffer.append("  method:").append(MonitorUtils.methodId(method)).append("\n");
        }
        buffer.append("  remoteAddr:").append(remoteAddr).append("\n")//
                .append("  httpMethod:").append(httpMethod).append("\n")//
                .append("  requestTime:").append(ndf.format(requestTime)).append("\n")//
                .append("  use:").append(use()).append("\n")//
                .append("  status:").append(status).append("\n")//
                .append("  arguments:").append(getJsonArguments()).append("\n")//
                .append("  result:").append(getJsonResult()).append("\n")//
                .append("  exception:").append(isException()).append("\n");
        if (isException()) {
            buffer.append("  exceptionMessage:").append(exception).append("\n");
        }
        return buffer.append("}").toString();
    }
}

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

import static com.harmony.umbrella.monitor.util.MonitorUtils.*;

import java.lang.reflect.Method;

import com.harmony.umbrella.monitor.MethodGraph;

/**
 * 基于方法监控的结果视图
 * 
 * @author wuxii@foxmail.com
 */
public class DefaultMethodGraph extends AbstractGraph implements MethodGraph {

    protected final Method method;
    protected Object target;

    public DefaultMethodGraph(Method method) {
        this(null, method, null);
    }

    public DefaultMethodGraph(Object target, Method method, Object[] args) {
        super(methodId(method));
        this.target = target;
        this.method = method;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object getMethodResult() {
        return result.get(METHOD_RESULT);
    }

    @Override
    public Object[] getMethodArguments() {
        return (Object[]) this.arguments.get(METHOD_ARGUMENT);
    }

    @Override
    public void setMethodResult(Object result) {
        this.result.put(METHOD_RESULT, result);
    }

    @Override
    public void setMethodArgumets(Object... arguments) {
        this.arguments.put(METHOD_ARGUMENT, arguments);
    }

}

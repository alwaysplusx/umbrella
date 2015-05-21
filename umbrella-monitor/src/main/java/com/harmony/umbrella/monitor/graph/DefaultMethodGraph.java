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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.harmony.umbrella.monitor.MethodMonitor.MethodGraph;
import com.harmony.umbrella.monitor.annotation.Monitored;
import com.harmony.umbrella.monitor.util.MonitorUtils;

/**
 * @author wuxii@foxmail.com
 */
public class DefaultMethodGraph extends AbstractGraph<Collection<Object>> implements MethodGraph {

    protected Object target;
    protected Method method;
    protected Collection<Object> arguments = new ArrayList<Object>();

    public DefaultMethodGraph() {
    }

    public DefaultMethodGraph(Object target, Method method, Object[] args) {
        this.identifie = MonitorUtils.methodIdentifie(method);
        this.target = target;
        this.method = method;
        Collections.addAll(arguments, args);
    }

    public Object getTarget() {
        return target;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
        this.identifie = MonitorUtils.methodIdentifie(method);
    }

    @Override
    public Object[] getArgs() {
        return arguments.toArray();
    }

    @Override
    public String getModule() {
        Monitored ann = method.getAnnotation(Monitored.class);
        if (ann != null) {
            return ann.module();
        }
        return null;
    }

    @Override
    public String getOperator() {
        Monitored ann = method.getAnnotation(Monitored.class);
        if (ann != null) {
            return ann.operator();
        }
        return null;
    }

    public void setArgs(Object[] args) {
        this.arguments.clear();
        Collections.addAll(arguments, args);
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public Collection<Object> getArguments() {
        return arguments;
    }

    public void setArguments(Collection<Object> arguments) {
        this.arguments = arguments;
    }
}

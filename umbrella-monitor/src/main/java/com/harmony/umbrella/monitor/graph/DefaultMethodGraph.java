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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.harmony.umbrella.monitor.MethodMonitor.MethodGraph;

/**
 * @author wuxii@foxmail.com
 */
public class DefaultMethodGraph extends AbstractGraph<Collection<Object>> implements MethodGraph {

    protected Object target;
    protected final Method method;
    protected Collection<Object> arguments = new ArrayList<Object>();

    public DefaultMethodGraph(Method method) {
        this(null, method, null);
    }

    public DefaultMethodGraph(Object target, Method method, Object[] args) {
        super(methodIdentifie(method));
        this.target = target;
        this.method = method;
        if (args != null) {
            Collections.addAll(arguments, args);
        }
    }

    public Object getTarget() {
        return target;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public Collection<Object> getRequestParam() {
        return Collections.unmodifiableCollection(arguments);
    }

    public void setArguments(Object[] arguments) {
        this.arguments.clear();
        Collections.addAll(this.arguments, arguments);
    }

    @Override
    public Object[] getArguments() {
        return arguments.toArray();
    }

}

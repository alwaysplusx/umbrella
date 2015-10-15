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
package com.harmony.umbrella.monitor.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Calendar;

import com.harmony.umbrella.monitor.GraphListener;
import com.harmony.umbrella.monitor.MethodGraph;
import com.harmony.umbrella.monitor.annotation.Monitored;
import com.harmony.umbrella.monitor.graph.DefaultMethodGraph;
import com.harmony.umbrella.util.MethodUtils;

/**
 * @author wuxii@foxmail.com
 */
public class JDKProxyMethodMonitor extends AbstractMethodMonitorInterceptor<ProxyInvocationContext> implements InvocationHandler {

    private final Object target;
    private boolean throwException;

    public JDKProxyMethodMonitor(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return monitor(new ProxyInvocationContext(proxy, method, args));
    }

    @Override
    protected Object aroundMonitor(Method method, ProxyInvocationContext ctx) throws Exception {
        Object result = null;

        DefaultMethodGraph graph = new DefaultMethodGraph(method);

        graph.setTarget(target);
        graph.setMethodArgumets(ctx.arguments);

        Monitored ann = method.getAnnotation(Monitored.class);
        if (ann != null) {
            applyMonitorInformation(graph, ann);
        }
        applyMethodRequestProperty(graph, target, method);

        graph.setRequestTime(Calendar.getInstance());

        try {

            result = process(ctx);

            graph.setResponseTime(Calendar.getInstance());
            applyMethodResponseProperty(graph, target, method);
            graph.setMethodResult(result);

        } catch (Exception e) {
            graph.setException(e);
            if (throwException) {
                throw e;
            }
        } finally {
            notifyGraphListeners(graph);
        }

        return result;
    }

    protected void notifyGraphListeners(DefaultMethodGraph graph) {
        for (GraphListener<MethodGraph> listener : graphListeners) {
            listener.analyze(graph);
        }
    }

    @Override
    protected Method getMethod(ProxyInvocationContext ctx) {
        return ctx.method;
    }

    @Override
    protected Object process(ProxyInvocationContext ctx) throws Exception {
        return MethodUtils.invokeMethod(ctx.method, target, ctx.arguments);
    }

    @Override
    public void destroy() {
    }

}

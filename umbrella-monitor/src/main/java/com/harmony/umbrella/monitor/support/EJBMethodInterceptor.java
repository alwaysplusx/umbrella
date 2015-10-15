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

import java.lang.reflect.Method;
import java.util.Calendar;

import javax.interceptor.InvocationContext;

import com.harmony.umbrella.monitor.GraphListener;
import com.harmony.umbrella.monitor.MethodGraph;
import com.harmony.umbrella.monitor.annotation.Monitored;
import com.harmony.umbrella.monitor.graph.DefaultMethodGraph;

/**
 * @author wuxii@foxmail.com
 */
public abstract class EJBMethodInterceptor extends AbstractMethodMonitorInterceptor<InvocationContext> {

    private boolean throwException = false;

    @Override
    protected Object aroundMonitor(Method method, InvocationContext ctx) throws Exception {
        Object result = null;

        DefaultMethodGraph graph = new DefaultMethodGraph(method);

        graph.setTarget(ctx.getTarget());
        graph.setMethodArgumets(ctx.getParameters());

        Monitored ann = method.getAnnotation(Monitored.class);
        if (ann != null) {
            applyMonitorInformation(graph, ann);
        }
        applyMethodRequestProperty(graph, ctx.getTarget(), method);

        graph.setRequestTime(Calendar.getInstance());

        try {

            result = process(ctx);

            graph.setResponseTime(Calendar.getInstance());
            applyMethodResponseProperty(graph, ctx.getTarget(), method);
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

    @Override
    protected Method getMethod(InvocationContext ctx) {
        return ctx.getMethod();
    }

    @Override
    protected Object process(InvocationContext ctx) throws Exception {
        return ctx.proceed();
    }

    protected void notifyGraphListeners(DefaultMethodGraph graph) {
        for (GraphListener<MethodGraph> listener : graphListeners) {
            listener.analyze(graph);
        }
    }

    @Override
    public void destroy() {
    }
}

package com.harmony.umbrella.monitor.support;

import java.lang.reflect.Method;
import java.util.Calendar;

import javax.interceptor.InvocationContext;

import com.harmony.umbrella.monitor.annotation.Monitor;
import com.harmony.umbrella.monitor.graph.DefaultMethodGraph;

/**
 * @author wuxii@foxmail.com
 */
public abstract class EJBMethodInterceptor extends AbstractMethodMonitorInterceptor<InvocationContext> {

    @Override
    protected Object aroundMonitor(Method method, InvocationContext ctx) throws Exception {
        Object result = null;

        Object target = ctx.getTarget();
        DefaultMethodGraph graph = new DefaultMethodGraph(target, method, ctx.getParameters());

        Monitor ann = method.getAnnotation(Monitor.class);
        if (ann != null) {
            applyMonitorInformation(graph, ann);
        }
        applyMethodRequestProperty(graph, target, method);

        graph.setRequestTime(Calendar.getInstance());

        try {

            result = process(ctx);

            graph.setResponseTime(Calendar.getInstance());
            graph.setMethodResult(result);
            applyMethodResponseProperty(graph, target, method);

        } catch (Exception e) {
            graph.setException(e);
            throw e;
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

}

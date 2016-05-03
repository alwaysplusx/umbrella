package com.harmony.umbrella.monitor.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Calendar;

import com.harmony.umbrella.monitor.annotation.Monitor;
import com.harmony.umbrella.monitor.graph.DefaultMethodGraph;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * @author wuxii@foxmail.com
 */
public class JDKProxyMethodMonitor extends AbstractMethodMonitorInterceptor<ProxyInvocationContext>implements InvocationHandler {

    private final Object target;

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

        Monitor ann = method.getAnnotation(Monitor.class);
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
            throw e;
        } finally {
            notifyGraphListeners(graph);
        }

        return result;
    }

    @Override
    protected Method getMethod(ProxyInvocationContext ctx) {
        return ctx.method;
    }

    @Override
    protected Object process(ProxyInvocationContext ctx) throws Exception {
        return ReflectionUtils.invokeMethod(ctx.method, target, ctx.arguments);
    }

    @Override
    public void destroy() {
    }

}

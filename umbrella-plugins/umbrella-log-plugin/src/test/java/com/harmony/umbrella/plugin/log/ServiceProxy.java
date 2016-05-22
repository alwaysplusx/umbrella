package com.harmony.umbrella.plugin.log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.harmony.umbrella.core.DefaultMethodGraph;

/**
 * @author wuxii@foxmail.com
 */
public class ServiceProxy implements InvocationHandler {

    private static final LoggingReport report = new LoggingReport();

    private Object target;

    private ServiceProxy(Object target) {
        this.target = target;
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Object target) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return (T) Proxy.newProxyInstance(cl, target.getClass().getInterfaces(), new ServiceProxy(target));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        DefaultMethodGraph graph = new DefaultMethodGraph(target, method, args);
        Object result = null;
        try {
            graph.setRequestTime(System.currentTimeMillis());
            result = method.invoke(target, args);
            graph.setResponseTime(System.currentTimeMillis());
            graph.setResult(result);
        } catch (Exception e) {
            graph.setThrowable(e);
        } finally {
            report.report(graph);
        }
        return result;
    }
}

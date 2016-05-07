package com.harmony.umbrella.core;

import java.lang.reflect.Method;

/**
 * @author wuxii@foxmail.com
 */
public class InvokerMethodInvocationContext implements MethodInvocationContext {

    private Invoker invoker = new DefaultInvoker();
    private Object target;
    private Method method;
    private Object[] parameters;

    private MethodGraph graph;

    public InvokerMethodInvocationContext(Object target, Method method, Object[] parameters) {
        this.target = target;
        this.method = method;
        this.parameters = parameters;
    }

    public InvokerMethodInvocationContext(Invoker invoker, Object target, Method method, Object[] parameters) {
        this.invoker = invoker;
        this.target = target;
        this.method = method;
        this.parameters = parameters;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public Class<?> getTargetClass() {
        return target == null ? null : target.getClass();
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public Object process() throws Throwable {
        DefaultMethodGraph graph = new DefaultMethodGraph(target, method, parameters);
        Object result = null;
        try {
            graph.setRequestTime(System.currentTimeMillis());
            result = invoker.invoke(target, method, parameters);
        } catch (Throwable e) {
            graph.setThrowable(e);
            throw e;
        } finally {
            graph.setRequestTime(System.currentTimeMillis());
            graph.setResult(result);
            this.graph = graph;
        }
        return result;
    }

    @Override
    public MethodGraph getMethodGraph() {
        return graph;
    }

    public Invoker getInvoker() {
        return invoker;
    }

    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

}

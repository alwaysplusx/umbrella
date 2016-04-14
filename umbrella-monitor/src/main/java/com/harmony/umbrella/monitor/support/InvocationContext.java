package com.harmony.umbrella.monitor.support;

import java.lang.reflect.Method;

import com.harmony.umbrella.monitor.MethodGraph;
import com.harmony.umbrella.monitor.graph.DefaultMethodGraph;

/**
 * @author wuxii@foxmail.com
 */
public abstract class InvocationContext {

    private final DefaultMethodGraph graph;

    public InvocationContext(Method method) {
        this.graph = new DefaultMethodGraph(method);
    }

    public InvocationContext(Object target, Method method, Object[] parameters) {
        this.graph = new DefaultMethodGraph(target, method, parameters);
    }

    public Method getMethod() {
        return graph.getMethod();
    }

    public Object getTarget() {
        return graph.getTarget();
    }

    public Object[] getParameter() {
        return graph.getParameters();
    }

    public void setTarget(Object target) {
        graph.setTarget(target);
    }

    public void setParameters(Object[] parameters) {
        graph.setParameters(parameters);
    }

    public Object process() throws Exception {
        try {
            // 记录执行时间
            graph.setRequestTime(System.currentTimeMillis());
            graph.setResult(doProcess());
            graph.setResponseTime(System.currentTimeMillis());
            // 记录执行结果
        } catch (Exception e) {
            graph.setThrowable(e);
            throw e;
        }
        return graph.getResult();
    }

    protected abstract Object doProcess() throws Exception;

    public MethodGraph toGraph() {
        return graph;
    }

}

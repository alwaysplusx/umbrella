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

    private static final long serialVersionUID = -3818216781456549358L;

    protected final transient Method method;
    protected transient Object target;

    public DefaultMethodGraph(Method method) {
        this(null, method, null);
    }

    public DefaultMethodGraph(Object target, Method method, Object[] args) {
        super(methodId(method));
        this.target = target;
        this.method = method;
        this.setMethodArgumets(args);
    }

    public Object getTarget() {
        return target;
    }

    @Override
    public Class<?> getTargetClass() {
        return target != null ? target.getClass() : null;
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

    public void setMethodResult(Object result) {
        this.result.put(METHOD_RESULT, result);
    }

    public void setMethodArgumets(Object... arguments) {
        this.arguments.put(METHOD_ARGUMENT, arguments);
    }

}

package com.harmony.umbrella.log.interceptor;

import java.lang.reflect.Method;

/**
 * @author wuxii@foxmail.com
 */
public abstract class InvocationContext {

    final Object target;
    final Method method;
    final Object[] parameters;
    Object result;

    public InvocationContext(Object target, Method method, Object[] parameters) {
        this.target = target;
        this.method = method;
        this.parameters = parameters;
    }

    public final Object process() throws Exception {
        this.result = doProcess();
        return result;
    }

    protected abstract Object doProcess() throws Exception;

}

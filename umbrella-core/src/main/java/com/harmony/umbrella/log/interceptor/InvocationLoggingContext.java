package com.harmony.umbrella.log.interceptor;

import java.lang.reflect.Method;

import javax.interceptor.InvocationContext;

import com.harmony.umbrella.log.template.LoggingContext;

/**
 * @author wuxii@foxmail.com
 */
public class InvocationLoggingContext extends LoggingContext {

    private InvocationContext invocationContext;

    private Object result;

    private Throwable exception;

    public InvocationLoggingContext(InvocationContext invocationContext) {
        this.invocationContext = invocationContext;
    }

    @Override
    public Object getTarget() {
        return invocationContext.getTarget();
    }

    @Override
    public Object[] getArguments() {
        return invocationContext.getParameters();
    }

    @Override
    protected Method getMethod() {
        return invocationContext.getMethod();
    }

    @Override
    public Object proceed() throws Exception {
        try {
            return result = invocationContext.proceed();
        } catch (Throwable e) {
            exception = e;
            throw e;
        }
    }

    @Override
    protected Object getResult() {
        return result;
    }

    @Override
    protected Throwable getException() {
        return exception;
    }

}

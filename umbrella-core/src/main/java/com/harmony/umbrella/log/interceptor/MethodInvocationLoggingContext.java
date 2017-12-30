package com.harmony.umbrella.log.interceptor;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;

import com.harmony.umbrella.log.template.LoggingContext;

/**
 * @author wuxii@foxmail.com
 */
public class MethodInvocationLoggingContext extends LoggingContext {

    private MethodInvocation methodInvocation;

    private Object result;
    private Throwable exception;

    public MethodInvocationLoggingContext(MethodInvocation methodInvocation) {
        this.methodInvocation = methodInvocation;
    }

    @Override
    public Object getTarget() {
        return methodInvocation.getThis();
    }

    @Override
    public Object[] getArguments() {
        return methodInvocation.getArguments();
    }

    @Override
    protected Method getMethod() {
        return methodInvocation.getMethod();
    }

    @Override
    protected Object getResult() {
        return result;
    }

    @Override
    protected Throwable getException() {
        return exception;
    }

    @Override
    public Object proceed() throws Throwable {
        try {
            return result = methodInvocation.proceed();
        } catch (Throwable e) {
            exception = e;
            throw e;
        }
    }

}

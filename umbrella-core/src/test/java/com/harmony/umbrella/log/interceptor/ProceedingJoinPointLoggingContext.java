package com.harmony.umbrella.log.interceptor;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import com.harmony.umbrella.log.template.LoggingContext;

public class ProceedingJoinPointLoggingContext extends LoggingContext {

    private ProceedingJoinPoint pjp;
    private Method method;

    private Object result;
    private Throwable exception;

    public ProceedingJoinPointLoggingContext(ProceedingJoinPoint pjp) {
        this(pjp, getMethod(pjp));
    }

    public ProceedingJoinPointLoggingContext(ProceedingJoinPoint pjp, Method method) {
        this.pjp = pjp;
        this.method = method;
    }

    @Override
    public Object getTarget() {
        return pjp.getTarget();
    }

    @Override
    public Object[] getArguments() {
        return pjp.getArgs();
    }

    @Override
    protected Method getMethod() {
        return method;
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
            return result = pjp.proceed();
        } catch (Throwable e) {
            exception = e;
            throw e;
        }
    }

    public static Method getMethod(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        return signature instanceof MethodSignature ? ((MethodSignature) signature).getMethod() : null;
    }
}
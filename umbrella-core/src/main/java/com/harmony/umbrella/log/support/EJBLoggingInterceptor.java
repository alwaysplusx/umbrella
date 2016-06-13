package com.harmony.umbrella.log.support;

import java.lang.reflect.Method;

import javax.interceptor.AroundInvoke;

import com.harmony.umbrella.log.expression.ValueContext;

/**
 * @author wuxii@foxmail.com
 */
public class EJBLoggingInterceptor extends LoggingInterceptor {

    @AroundInvoke
    public Object interceptor(javax.interceptor.InvocationContext context) throws Exception {
        return logging(new EJBInvocationContext(context));
    }

    @Override
    protected ValueContext createValueContext() {
        return null;
    }

    private class EJBInvocationContext implements InvocationContext {

        private final javax.interceptor.InvocationContext context;

        public EJBInvocationContext(javax.interceptor.InvocationContext context) {
            this.context = context;
        }

        @Override
        public Object getTarget() {
            return context.getTarget();
        }

        @Override
        public Method getMethod() {
            return context.getMethod();
        }

        @Override
        public Object[] getParameters() {
            return context.getParameters();
        }

        @Override
        public Object proceed() throws Exception {
            return context.proceed();
        }

    }

}

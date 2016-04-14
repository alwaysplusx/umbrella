package com.harmony.umbrella.monitor.support;

import javax.interceptor.AroundInvoke;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractEJBMonitorInterceptor extends AbstractMonitorInterceptor<javax.interceptor.InvocationContext> {

    @Override
    protected InvocationContext convert(javax.interceptor.InvocationContext context) {
        return new EJBInvocationContext(context);
    }

    @AroundInvoke
    public Object interceptor(javax.interceptor.InvocationContext context) throws Exception {
        return doInterceptor(convert(context));
    }

    static final class EJBInvocationContext extends com.harmony.umbrella.monitor.support.InvocationContext {

        private final javax.interceptor.InvocationContext invocationContext;

        public EJBInvocationContext(javax.interceptor.InvocationContext context) {
            super(context.getTarget(), context.getMethod(), context.getParameters());
            this.invocationContext = context;
        }

        @Override
        protected Object doProcess() throws Exception {
            return invocationContext.proceed();
        }
    }
}

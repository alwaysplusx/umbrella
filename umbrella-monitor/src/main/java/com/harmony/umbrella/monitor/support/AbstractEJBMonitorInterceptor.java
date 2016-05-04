package com.harmony.umbrella.monitor.support;

import javax.interceptor.AroundInvoke;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.CurrentContext;

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

    protected HttpServletResponse getResponse() {
        return null;
    }

    protected HttpServletRequest getRequest() {
        return null;
    }

    protected CurrentContext getCurrentContext() {
        return ApplicationContext.getApplicationContext().getCurrentContext();
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

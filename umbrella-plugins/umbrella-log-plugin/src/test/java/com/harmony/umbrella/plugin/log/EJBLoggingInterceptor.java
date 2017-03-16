package com.harmony.umbrella.plugin.log;

import javax.interceptor.AroundInvoke;

import com.harmony.umbrella.plugin.log.expression.LogValueContext;
import com.harmony.umbrella.plugin.log.expression.ValueContext;
import com.harmony.umbrella.plugin.log.interceptor.AbstractLoggingInterceptor;

/**
 * @author wuxii@foxmail.com
 */
public class EJBLoggingInterceptor extends AbstractLoggingInterceptor {

    @AroundInvoke
    public Object interceptor(javax.interceptor.InvocationContext context) throws Exception {
        return aroundLogging(context);
    }

    @Override
    protected ValueContext createValueContext() {
        return new LogValueContext();
    }

}

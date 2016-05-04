package com.harmony.umbrella.plugin.log.interceptor;

import com.harmony.umbrella.monitor.MethodGraphReporter;
import com.harmony.umbrella.monitor.support.AbstractEJBMonitorInterceptor;
import com.harmony.umbrella.monitor.support.InvocationContext;

/**
 * @author wuxii@foxmail.com
 */
public class EJBLoggingInterceptor extends AbstractEJBMonitorInterceptor {

    private MethodGraphReporter reporter;

    @Override
    protected Object doInterceptor(InvocationContext invocationContext) throws Exception {
        try {
            return invocationContext.process();
        } finally {
            reporter.report(invocationContext.toGraph(), getRequest(), getResponse());
        }
    }

}

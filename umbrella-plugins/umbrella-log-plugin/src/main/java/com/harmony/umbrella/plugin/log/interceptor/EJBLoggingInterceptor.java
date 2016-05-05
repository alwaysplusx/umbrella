package com.harmony.umbrella.plugin.log.interceptor;

import com.harmony.umbrella.monitor.MethodGraphReport;
import com.harmony.umbrella.monitor.support.AbstractEJBMonitorInterceptor;
import com.harmony.umbrella.monitor.support.InvocationContext;
import com.harmony.umbrella.plugin.log.LoggingReport;

/**
 * @author wuxii@foxmail.com
 */
public class EJBLoggingInterceptor extends AbstractEJBMonitorInterceptor {

    private MethodGraphReport reporter = new LoggingReport();

    @Override
    protected Object doInterceptor(InvocationContext invocationContext) throws Exception {
        try {
            return invocationContext.process();
        } finally {
            reporter.report(invocationContext.toGraph(), getRequest(), getResponse());
        }
    }

}

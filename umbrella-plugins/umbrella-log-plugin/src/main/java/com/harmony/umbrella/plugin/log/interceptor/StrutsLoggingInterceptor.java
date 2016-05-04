package com.harmony.umbrella.plugin.log.interceptor;

import com.harmony.umbrella.monitor.MethodGraphReporter;
import com.harmony.umbrella.monitor.support.AbstractStrutsMonitorInterceptor;
import com.harmony.umbrella.monitor.support.InvocationContext;
import com.harmony.umbrella.plugin.log.LoggingReport;

/**
 * @author wuxii@foxmail.com
 */
public class StrutsLoggingInterceptor extends AbstractStrutsMonitorInterceptor {

    private static final long serialVersionUID = 2652205290781605828L;

    private MethodGraphReporter reporter;

    @Override
    public void init() {
        reporter = new LoggingReport();
    }

    @Override
    protected Object doInterceptor(InvocationContext invocationContext) throws Exception {
        try {
            return invocationContext.process();
        } finally {
            reporter.report(invocationContext.toGraph(), getRequest(), getResponse());
        }
    }

}

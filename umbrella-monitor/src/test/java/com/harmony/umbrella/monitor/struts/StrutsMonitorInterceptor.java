package com.harmony.umbrella.monitor.struts;

import com.harmony.umbrella.monitor.StrutsMonitorInterceptorTest;
import com.harmony.umbrella.monitor.support.AbstractStrutsMonitorInterceptor;
import com.harmony.umbrella.monitor.support.InvocationContext;

public class StrutsMonitorInterceptor extends AbstractStrutsMonitorInterceptor {

    private static final long serialVersionUID = 1L;

    @Override
    public void init() {
    }

    @Override
    protected Object doInterceptor(InvocationContext invocationContext) throws Exception {
        StrutsMonitorInterceptorTest.INTERCEPTOR_FLAG = true;
        return invocationContext.process();
    }

    @Override
    public void destroy() {
    }

}
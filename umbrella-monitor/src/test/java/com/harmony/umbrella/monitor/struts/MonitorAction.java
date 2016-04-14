package com.harmony.umbrella.monitor.struts;

import com.harmony.umbrella.monitor.StrutsMonitorInterceptorTest;
import com.opensymphony.xwork2.ActionSupport;

public class MonitorAction extends ActionSupport {

    private static final long serialVersionUID = 1L;

    @Override
    public String execute() throws Exception {
        StrutsMonitorInterceptorTest.ACTION_FLAG = true;
        return super.execute();
    }
}

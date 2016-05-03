package com.harmony.umbrella.monitor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

import com.harmony.umbrella.monitor.support.EJBMethodInterceptor;

/**
 * @author wuxii@foxmail.com
 */
@Interceptor
public class LoggingInterceptor extends EJBMethodInterceptor {

    private static final Log log = Logs.getLog(LoggingInterceptor.class);

    public LoggingInterceptor() {
        this.policy = MonitorPolicy.All;
        this.graphListeners.add(new GraphListener<MethodGraph>() {

            @Override
            public void analyze(MethodGraph graph) {
                log.info("{}", graph);
            }
        });
    }

    @AroundInvoke
    public Object log(InvocationContext ctx) throws Exception {
        return monitor(ctx);
    }

}

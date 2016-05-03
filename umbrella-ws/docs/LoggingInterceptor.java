package com.harmony.umbrella.ws.ext;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import com.harmony.umbrella.monitor.GraphListener;
import com.harmony.umbrella.monitor.MethodGraph;
import com.harmony.umbrella.monitor.ext.LogUtils;
import com.harmony.umbrella.monitor.support.EJBMethodInterceptor;

/**
 * 服务端webservice日志监控工具
 * 
 * @author wuxii@foxmail.com
 */
@Interceptor
public class LoggingInterceptor extends EJBMethodInterceptor {

    public LoggingInterceptor() {
        this.policy = MonitorPolicy.All;
        this.graphListeners.add(new GraphListener<MethodGraph>() {

            @Override
            public void analyze(MethodGraph graph) {
                LogUtils.log(graph);
            }

        });
    }

    @AroundInvoke
    public Object log(InvocationContext ctx) throws Exception {
        return monitor(ctx);
    }

}

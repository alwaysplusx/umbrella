package com.harmony.umbrella.log.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import com.harmony.umbrella.log.annotation.Logging;
import com.harmony.umbrella.log.template.LoggingContext;
import com.harmony.umbrella.log.template.LoggingResult;
import com.harmony.umbrella.log.template.MethodLogRecorder;

/**
 * @author wuxii@foxmail.com
 */
@Logging
@Interceptor
public class LoggingInterceptor {

    /**
     * 挂起日志监控标志
     */
    protected boolean suspended;

    private MethodLogRecorder methodLogRecorder;

    @AroundInvoke
    public Object logging(InvocationContext context) throws Exception {
        if (suspended) {
            return context.proceed();
        }
        LoggingContext loggingContext = newLoggingContext(context);
        LoggingResult loggingResult = getMethodLogRecorder().aroundProceed(loggingContext);
        System.out.println(loggingResult.getLogMessage());
        return loggingResult.getResult();
    }

    protected LoggingContext newLoggingContext(InvocationContext context) {
        return new InvocationLoggingContext(context);
    }

    protected MethodLogRecorder getMethodLogRecorder() {
        if (methodLogRecorder == null) {
            methodLogRecorder = new MethodLogRecorder();
        }
        return methodLogRecorder;
    }

    public void setMethodLogRecorder(MethodLogRecorder methodLogRecorder) {
        this.methodLogRecorder = methodLogRecorder;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

}

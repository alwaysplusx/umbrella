package com.harmony.umbrella.log.interceptor;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.log.annotation.Logging;
import com.harmony.umbrella.log.template.LoggingContext;
import com.harmony.umbrella.log.template.LoggingResult;
import com.harmony.umbrella.log.template.MethodLogRecorder;

/**
 * @author wuxii@foxmail.com
 */
public class LoggingInterceptor implements MethodInterceptor {

    /**
     * 挂起日志监控标志
     */
    private boolean suspended;

    /**
     * 注解模式(仅拦截有注解的方法)
     */
    private boolean annotationMode = true;

    private MethodLogRecorder recorder;

    protected Log log = Logs.getLog(LoggingInterceptor.class);

    public LoggingInterceptor() {
        this(new MethodLogRecorder());
    }

    public LoggingInterceptor(MethodLogRecorder recorder) {
        this.recorder = recorder;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (suspended) {
            return invocation.proceed();
        }
        Method method = invocation.getMethod();
        Logging ann = method.getAnnotation(Logging.class);
        if (ann == null && annotationMode) {
            return invocation.proceed();
        }
        return monitor(new MethodInvocationLoggingContext(invocation));
    }

    protected Object monitor(LoggingContext context) throws Throwable {
        LoggingResult loggingResult = recorder.aroundProceed(context);
        // logging message always
        logging(loggingResult.getLogMessage());
        // problem can't be handled throw it out
        if (loggingResult.getException() != null && !loggingResult.isProblemHandled()) {
            throw loggingResult.getException();
        }
        return loggingResult.getResult();
    }

    protected void logging(LogInfo logMessage) {
        log.log(logMessage);
    }

    public MethodLogRecorder getMethodLogRecorder() {
        return recorder;
    }

    public void setMethodLogRecorder(MethodLogRecorder methodLogRecorder) {
        recorder = methodLogRecorder;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public boolean isAnnotationMode() {
        return annotationMode;
    }

    public void setAnnotationMode(boolean annotationMode) {
        this.annotationMode = annotationMode;
    }

}

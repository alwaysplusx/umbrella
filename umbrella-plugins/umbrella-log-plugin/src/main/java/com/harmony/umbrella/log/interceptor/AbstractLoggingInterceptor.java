package com.harmony.umbrella.log.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.CurrentContext;
import com.harmony.umbrella.log.LogMessage;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.log.Message;
import com.harmony.umbrella.log.annotation.Log;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractLoggingInterceptor<C> {

    private boolean raiseError = true;

    protected ApplicationContext applicationContext;

    protected abstract InvocationContext convert(C c);

    protected abstract Message newMessage(InvocationContext ctx);

    public void init() {
        this.applicationContext = ApplicationContext.getApplicationContext();
    }

    protected Object logging(C c) throws Exception {
        InvocationContext ctx = convert(c);
        if (ctx.method == null) {
            return ctx.process();
        }

        Method method = ctx.method;
        Object target = ctx.target;

        Log ann = method.getAnnotation(Log.class);

        LogMessage logMessage = LogMessage.create(Logs.getLog(target.getClass()));

        // TODO if @Log is null do default logging otherwise business logging
        if (ann != null) {
            logMessage.action(ann.action())
                    .module(ann.module())
                    .bizModule(ann.bizModule())
                    .level(ann.level());
        }

        if (applicationContext.hasCurrentContext()) {
            CurrentContext cc = applicationContext.getCurrentContext();
            logMessage.operatorId((Serializable) cc.getUserId())
                    .operator(cc.getUsername());
        }

        logMessage.start();
        Object result = null;
        try {
            result = ctx.process();
            logMessage.result(result);
        } catch (Exception e) {
            logMessage.exception(e);
            if (raiseError) {
                throw e;
            }
        }
        logMessage.finish();
        // 设置日志主要内容
        logMessage.message(newMessage(ctx)).log();

        return result;
    }

    public boolean isRaiseError() {
        return raiseError;
    }

    public void setRaiseError(boolean raiseError) {
        this.raiseError = raiseError;
    }

}

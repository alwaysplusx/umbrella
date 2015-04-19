package com.harmony.umbrella.jaxws.cxf.interceptor;

import static org.apache.cxf.phase.Phase.*;

import java.lang.reflect.Method;

import org.apache.cxf.message.Message;

public class HandleInFaultInterceptor extends AbstractHandleInterceptor {

    public HandleInFaultInterceptor() {
        super(INVOKE);
    }

    @Override
    protected void handleServerValidation(Message message, Object resourceInstance, Method method, Object[] args) {
    }

    @Override
    protected void handleProxyValidation(Message message, Method method, Object[] args) {
    }

}

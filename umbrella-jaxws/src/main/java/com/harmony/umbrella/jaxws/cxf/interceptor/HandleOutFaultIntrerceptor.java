package com.harmony.umbrella.jaxws.cxf.interceptor;

import static org.apache.cxf.phase.Phase.*;

import java.lang.reflect.Method;

import org.apache.cxf.message.Message;

public class HandleOutFaultIntrerceptor extends AbstractHandleInterceptor {

    public HandleOutFaultIntrerceptor() {
        super(UNMARSHAL);
    }

    @Override
    protected void handleServerValidation(Message message, Object resourceInstance, Method method, Object[] args) {
    }

    @Override
    protected void handleProxyValidation(Message message, Method method, Object[] args) {
    }

}

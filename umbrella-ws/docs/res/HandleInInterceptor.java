package com.harmony.umbrella.ws.cxf.interceptor;

import java.lang.reflect.Method;

import org.apache.cxf.message.Message;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.ws.Phase;
import com.harmony.umbrella.ws.util.HandleMethodInvoker;

public class HandleInInterceptor extends AbstractHandleInterceptor {

    private static final Log log = Logs.getLog(HandleInInterceptor.class);

    public HandleInInterceptor() {
        // super(POST_UNMARSHAL);
        super(Phase.PRE_INVOKE.render());
    }

    @Override
    protected void handleServer(Message message, Object resourceInstance, Method method, Object[] args) {
        log.debug("handle server {} parameters {}", StringUtils.getMethodId(method), args);
        try {
            HandleMethodInvoker[] hms = finder.findHandleMethods(method, getJaxWsPhase());
            for (HandleMethodInvoker hm : hms) {
                System.out.println(hm);
            }
        } finally {
            PREVIOUS_SERVER_MESSAGE.set(message);
        }
    }

    @Override
    protected void handleProxy(Message message, Method method, Object[] responseArgs) {
        // try {
        // Message requestMessage = PREVIOUS_PROXY_MESSAGE.get();
        // if (requestMessage == null) {
        // log.warn("please add handle out interceptor");
        // return;
        // }
        // final Object[] requestArgs = resolverArguments(requestMessage);
        // Method[] handlMethods = finder.findHandlerMethod(method,
        // Phase.POST_INVOKE);
        // List<Object> argList = new ArrayList<Object>();
        // Collections.addAll(argList, responseArgs);
        // Collections.addAll(argList, requestArgs);
        // for (Method m : handlMethods) {
        // Object target = beanLoader.loadBean(m.getDeclaringClass());
        // try {
        // invoker.invok(target, m, argList.toArray());
        // } catch (InvokException e) {
        // log.error("", e);
        // }
        // }
        // } finally {
        // PREVIOUS_PROXY_MESSAGE.set(null);
        // }
    }

}

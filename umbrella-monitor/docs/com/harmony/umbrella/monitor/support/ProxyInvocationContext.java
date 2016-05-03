package com.harmony.umbrella.monitor.support;

import java.lang.reflect.Method;

/**
 * @author wuxii@foxmail.com
 */
class ProxyInvocationContext {

    Object proxy;
    Method method;
    Object[] arguments;

    public ProxyInvocationContext(Object proxy, Method method, Object[] arguments) {
        this.proxy = proxy;
        this.method = method;
        this.arguments = arguments;
    }

}

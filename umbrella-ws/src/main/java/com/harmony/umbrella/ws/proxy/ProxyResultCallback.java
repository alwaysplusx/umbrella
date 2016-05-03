package com.harmony.umbrella.ws.proxy;

/**
 * @author wuxii@foxmail.com
 */
public interface ProxyResultCallback<T, R extends Result> extends ProxyCallback<T, R> {

    R newResult(Object result, boolean wrapped);

}

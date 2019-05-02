package com.harmony.umbrella.log.interceptor;

/**
 * marker interface
 *
 * @author wuxii
 */
public interface LogInterceptor<INVOCATION> {

    Object interceptor(INVOCATION invocation) throws Throwable;

}

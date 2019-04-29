package com.harmony.umbrella.log.interceptor;

import org.aopalliance.intercept.MethodInvocation;

/**
 * @author wuxii
 */
public interface LogInterceptorFilter {

    boolean accept(MethodInvocation invocation);

}

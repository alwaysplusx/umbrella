package com.harmony.umbrella.plugin.log.interceptor;

import java.lang.reflect.Method;

/**
 * 
 * @author wuxii@foxmail.com
 */
public interface InvocationContext {

    Object getTarget();
    
    Class<?> getTargetClass();

    Method getMethod();

    Object[] getParameters();

    Object proceed() throws Exception;

}
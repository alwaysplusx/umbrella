package com.harmony.umbrella.log.support;

import java.lang.reflect.Method;

/**
 * 
 * @author wuxii@foxmail.com
 */
public interface InvocationContext {

    Object getTarget();

    Method getMethod();

    Object[] getParameters();

    Object proceed() throws Exception;

}
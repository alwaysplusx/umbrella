package com.harmony.umbrella.core;

import java.lang.reflect.Method;

/**
 * @author wuxii@foxmail.com
 */
public interface MethodInvocationContext {

    Object getTarget();

    Class<?> getTargetClass();

    Method getMethod();

    Object[] getParameters();

    Object process() throws Throwable;

    MethodGraph getMethodGraph();

}

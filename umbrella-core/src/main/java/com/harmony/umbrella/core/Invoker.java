package com.harmony.umbrella.core;

import java.lang.reflect.Method;

/**
 * 反射执行目标类的方法
 *
 * @author wuxii@foxmail.com
 */
public interface Invoker {

    /**
     * 执行目标方法
     *
     * @param target
     *         目标实例
     * @param method
     *         调用的方法
     * @param args
     *         方法参数
     * @return 反射执行后的结果
     * @throws InvokeException
     */
    Object invoke(Object target, Method method, Object[] args) throws InvokeException;

}

package com.harmony.umbrella.ws.util;

import java.lang.reflect.Method;
import java.util.Map;

import com.harmony.umbrella.core.InvokeException;
import com.harmony.umbrella.core.Invoker;
import com.harmony.umbrella.ws.Phase;

/**
 * 专为{@linkplain com.harmony.umbrella.ws.annotation.Handler.HandleMethod}提供的invoker
 * 
 * @author wuxii@foxmail.com
 */
public interface HandleMethodInvoker extends Invoker {

    /**
     * 执行handleMethod
     * 
     * @param target
     *            目标实例
     * @param args
     *            参数
     * @return
     * @throws InvokeException
     */
    Object invokeHandleMethod(Object target, Object[] args) throws InvokeException;

    /**
     * 执行handleMethod
     * 
     * @param target
     *            目标实例
     * @param args
     *            参数
     * @param contextMap
     * @return
     * @throws InvokeException
     */
    Object invokeHandleMethod(Object target, Object[] args, Map<String, Object> contextMap) throws InvokeException;

    /**
     * handler class
     */
    Class<?> getHandlerClass();

    /**
     * handleMethod
     */
    Method getHandleMethod();

    /**
     * 对应的执行周期
     */
    Phase getPhase();

    /**
     * 最后的参数是否要带上ContextMap
     */
    boolean isEndWithMap();

    /**
     * 设置异常
     * 
     * @param throwable
     */
    void setThrowable(Throwable throwable);

    /**
     * 设置结果
     * 
     * @param result
     */
    void setResult(Object result);

    /**
     * 设置contextMap
     * 
     * @param contextMap
     */
    void setContextMap(Map<String, Object> contextMap);

}

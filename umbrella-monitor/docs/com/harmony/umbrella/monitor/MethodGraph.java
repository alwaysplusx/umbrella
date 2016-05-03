package com.harmony.umbrella.monitor;

import java.lang.reflect.Method;

/**
 * 方法监控结果视图
 */
public interface MethodGraph extends Graph {
    /**
     * method 拦截内部属性
     */
    String METHOD_PROPERTY = MethodGraph.class.getName() + ".METHOD_PROPERTY";
    /**
     * 拦截方法的返回值
     */
    String METHOD_RESULT = MethodGraph.class.getName() + ".METHOD_RESULT";
    /**
     * 拦截方法的请求参数
     */
    String METHOD_ARGUMENT = MethodGraph.class.getName() + ".METHOD_ARGUMENT";

    /**
     * 方法的执行目标
     * 
     * @return target
     */
    Object getTarget();

    /**
     * 获取目标类
     * 
     * @return target class
     */
    Class<?> getTargetClass();

    /**
     * 拦截的方法
     * 
     * @return method
     */
    Method getMethod();

    /**
     * 拦截方法的请求参数
     * 
     * @return 方法的参数
     */
    Object[] getMethodArguments();

    /**
     * 方法的返回值
     * 
     * @return 返回值
     */
    Object getMethodResult();

}
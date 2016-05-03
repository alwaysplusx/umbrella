package com.harmony.umbrella.monitor;

import java.lang.reflect.Method;

/**
 * 方法监控结果视图
 */
public interface MethodGraph extends Graph {

    String GRAPH_TYPE = "method";

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
    Object[] getParameters();

    /**
     * 方法的返回值
     *
     * @return 返回值
     */
    Object getResult();

}
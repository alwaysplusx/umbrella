package com.harmony.umbrella.core;

import java.lang.reflect.Method;

/**
 * 方法监控结果视图
 */
public interface MethodGraph {

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

    /**
     * 请求时间, 默认为创建Graph对象时间
     *
     * @return 请求时间
     */
    long getRequestTime();

    /**
     * 系统应答时间
     *
     * @return 应答时间
     */
    long getResponseTime();

    /**
     * 计算耗时
     * 
     * @return
     */
    long use();

    /**
     * 异常的原因
     *
     * @return exception, if not exception return null
     */
    Throwable getThrowable();

    /**
     * 判断是否异常
     * 
     * @return
     */
    boolean isThrowable();

    /**
     * 监控的整体描述
     *
     * @return 监控的描述
     */
    String getDescription();

}
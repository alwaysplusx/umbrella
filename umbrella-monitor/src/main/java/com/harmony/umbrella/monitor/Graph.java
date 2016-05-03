package com.harmony.umbrella.monitor;

import com.harmony.umbrella.monitor.Monitor.MonitorPolicy;

/**
 * 监控记录
 *
 * @author wuxii@foxmail.com
 */
public interface Graph {

    /**
     * 监控的资源标识
     *
     * @return 资源id
     */
    String getGraphId();

    /**
     * 监视的类型
     *
     * @return
     */
    String getGraphType();

    /**
     * 监控级别
     *
     * @return 监控级别
     */
    MonitorPolicy getPolicy();

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

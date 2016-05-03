package com.harmony.umbrella.monitor;

import java.util.Calendar;
import java.util.Map;

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
    String getIdentifier();

    /**
     * 监控的模块
     * 
     * @return 模块名称
     */
    String getMonitorName();

    MonitorPolicy getPolicy();

    /**
     * 请求时间, 默认为创建Graph对象时间
     * 
     * @return 请求时间
     */
    Calendar getRequestTime();

    /**
     * 系统应答时间
     * 
     * @return 应答时间
     */
    Calendar getResponseTime();

    /**
     * 请求耗时 单位毫秒(ms)
     * 
     * @return -1 无法计算耗时
     */
    long use();

    /**
     * 根据key获取请求graph中的请求参数
     * 
     * @param key
     *            请求参数key
     * @return value
     */
    Object getArgument(String key);

    /**
     * 设置请求参数的key和对应值
     * 
     * @param key
     *            请求参数的key
     * @param value
     *            请求参数的value
     */
    void putArgument(String key, Object value);

    /**
     * 请求参数
     * 
     * @return 请求参数
     */
    Map<String, Object> getArguments();

    /**
     * 返回结果
     * 
     * @param key
     *            结果key
     * @return 监控返回的结果
     */
    Object getResult(String key);

    /**
     * 设置监控结果值
     * 
     * @param key
     *            结果key
     * @param value
     *            结果value
     */
    void putResult(String key, Object value);

    /**
     * 获取监控结果
     * 
     * @return 监控结果
     */
    Map<String, Object> getResults();

    /**
     * 得到json格式的结果
     * 
     * @return 监控结果
     */
    String getJsonResult();

    /**
     * 得到json格式的请求参数
     * 
     * @return 监控的请求参数
     */
    String getJsonArguments();

    /**
     * 请求中是否有异常
     * 
     * @return true is exception
     */
    boolean isException();

    /**
     * 异常的原因
     * 
     * @return exception, if not exception return null
     */
    Exception getException();

    /**
     * 监控的整体描述
     * 
     * @return 监控的描述
     */
    String getDescription();

}

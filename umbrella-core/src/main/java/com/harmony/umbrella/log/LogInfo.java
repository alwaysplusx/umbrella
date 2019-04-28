package com.harmony.umbrella.log;

import java.io.Serializable;
import java.util.Date;

/**
 * 日志信息Bean
 *
 * @author wuxii@foxmail.com
 */
public interface LogInfo extends Serializable {

    /**
     * openTrace spanId
     *
     * @return
     */
    String getSpanId();

    /**
     * openTrace traceId
     *
     * @return
     */
    String getTraceId();

    /**
     * 日志模块
     *
     * @return module
     */
    String getModule();

    /**
     * 操作名称
     *
     * @return action
     */
    String getAction();

    /**
     * 业务日志数据主键
     *
     * @return key
     */
    Object getKey();

    /**
     * 日志消息内容
     *
     * @return message
     */
    String getMessage();

    /**
     * 记录开始事件
     *
     * @return request time
     */
    Date getRequestTime();

    /**
     * 记录结束事件
     *
     * @return response time
     */
    Date getResponseTime();

    /**
     * 日志的异常信息
     *
     * @return exception
     */
    Throwable getThrowable();

    /**
     * 日志级别
     *
     * @return level
     */
    Level getLevel();

    /**
     * 当前操作的用户
     *
     * @return 用户id
     */
    Object getUserId();

    /**
     * 操作的用户名
     *
     * @return 用户名
     */
    String getUsername();

    /**
     * 操作的线程
     *
     * @return thread
     */
    String getThread();

    /**
     * 当前调用的栈帧
     *
     * @return thread frame
     */
    String getThreadFrame();

}

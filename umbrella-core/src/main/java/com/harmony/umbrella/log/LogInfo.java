package com.harmony.umbrella.log;

import java.util.Date;
import java.util.Map;

import com.harmony.umbrella.log.Level.StandardLevel;
import com.harmony.umbrella.log.annotation.Logging.LogType;

/**
 * 日志信息Bean
 * 
 * @author wuxii@foxmail.com
 */
public interface LogInfo {

    /**
     * 日志模块
     * 
     * @return 日志模块
     */
    String getModule();

    /**
     * 操作名称
     * 
     * @return 操作名称
     */
    String getAction();

    /**
     * 日志消息内容
     * 
     * @return 日志消息内容
     */
    Message getMessage();

    /**
     * 日志的异常信息
     * 
     * @return 日志的异常信息
     */
    Throwable getThrowable();

    /**
     * 日志级别
     * 
     * @return 日志级别
     */
    StandardLevel getLevel();

    /**
     * 业务日志的结果
     * 
     * @return 业务日志的结果
     */
    Object getResult();

    /**
     * 记录开始事件
     * 
     * @return 记录开始事件
     */
    Date getRequestTime();

    /**
     * 记录结束事件
     * 
     * @return 记录结束事件
     */
    Date getResponseTime();

    /**
     * 操作人
     * 
     * @return 操作人
     */
    String getOperatorName();

    /**
     * 操作人id
     * 
     * @return 操作人id
     */
    Object getOperatorId();

    /**
     * 所操作的客户端地址，如:IP
     * 
     * @return ip
     */
    String getOperatorHost();

    /**
     * 业务日志数据主键
     * 
     * @return key
     */
    Object getKey();

    /**
     * 日志info的类型，有系统日志与业务日志之分
     * 
     * @return logType
     */
    LogType getType();

    /**
     * 操作栈，操作位于程序的位置
     * 
     * @return stack
     */
    String getStackLocation();

    /**
     * 操作的线程
     * 
     * @return thread name
     */
    String getThreadName();

    /**
     * 日志context
     * 
     * @return context
     */
    Map<String, Object> getContext();

}

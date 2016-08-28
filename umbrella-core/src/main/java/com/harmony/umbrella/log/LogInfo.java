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
     * @return
     */
    String getModule();

    /**
     * 操作名称
     * 
     * @return
     */
    String getAction();

    /**
     * 业务日志数据主键
     * 
     * @return
     */
    Object getKey();

    /**
     * 日志消息内容
     * 
     * @return
     */
    Message getMessage();

    /**
     * 业务日志的结果
     * 
     * @return
     */
    Object getResult();

    /**
     * 记录开始事件
     * 
     * @return
     */
    Date getRequestTime();

    /**
     * 记录结束事件
     * 
     * @return
     */
    Date getResponseTime();

    /**
     * 日志的异常信息
     * 
     * @return
     */
    Throwable getThrowable();

    /**
     * 日志级别
     * 
     * @return
     */
    StandardLevel getLevel();

    /**
     * 日志info的类型，有系统日志与业务日志之分
     * 
     * @return
     */
    LogType getType();

    /**
     * 操作人
     * 
     * @return
     */
    String getOperatorName();

    /**
     * 操作人id
     * 
     * @return
     */
    Object getOperatorId();

    /**
     * 所操作的客户端地址，如:IP
     * 
     * @return
     */
    String getOperatorHost();

    /**
     * 操作栈，操作位于程序的位置
     * 
     * @return
     */
    String getStackLocation();

    /**
     * 操作的线程
     * 
     * @return
     */
    String getThreadName();

    /**
     * 日志context
     * 
     * @return
     */
    Map<String, Object> getContext();

}

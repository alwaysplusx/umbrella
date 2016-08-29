package com.harmony.umbrella.log;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.harmony.umbrella.log.Level.StandardLevel;

/**
 * 日志信息Bean
 * 
 * @author wuxii@foxmail.com
 */
public interface LogInfo extends Serializable {

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
    String getMessage();

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

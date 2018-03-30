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
     * 日志的统一id
     * 
     * @return messageId
     */
    String getMessageId();

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
     * 业务日志的结果
     * 
     * @return result
     */
    Object getResult();

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
    StandardLevel getLevel();

    /**
     * 操作人
     * 
     * @return username
     */
    String getUsername();

    /**
     * 操作人id
     * 
     * @return user id
     */
    Object getUserId();

    /**
     * 用户所在的地址，如:IP
     * 
     * @return user host
     */
    String getHost();

    /**
     * 日志发送的地点(方法栈)
     * 
     * @return log location
     */
    String getLocation();

    /**
     * 操作的线程
     * 
     * @return thread
     */
    String getThread();

    /**
     * 日志context
     * 
     * @return context
     */
    Map<String, Object> getContext();

}

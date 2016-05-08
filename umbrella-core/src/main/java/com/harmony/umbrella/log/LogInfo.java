package com.harmony.umbrella.log;

import java.util.Date;
import java.util.Map;

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
     * 日志消息内容
     * 
     * @return
     */
    Message getMessage();

    Object getId();

    /**
     * 日志的异常信息
     * 
     * @return
     */
    Throwable getException();

    /**
     * 日志级别
     * 
     * @return
     */
    Level getLevel();

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
    Date getStartTime();

    /**
     * 记录结束事件
     * 
     * @return
     */
    Date getFinishTime();

    /**
     * 操作人
     * 
     * @return
     */
    String getOperator();

    /**
     * 操作人id
     * 
     * @return
     */
    Object getOperatorId();

    /**
     * 检查是否有异常
     * 
     * @return
     */
    boolean isException();

    /**
     * 记录耗时
     * 
     * @return
     */
    long use();

    /**
     * 操作栈，操作位于程序的位置
     * 
     * @return
     */
    String getStack();

    /**
     * 操作的线程
     * 
     * @return
     */
    String getThreadName();

    /**
     * 是否系统日志
     * 
     * @return
     */
    boolean isSystem();

    /**
     * 
     * @return
     */
    Map<String, Object> getContext();

}

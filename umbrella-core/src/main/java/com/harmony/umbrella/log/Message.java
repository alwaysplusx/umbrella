package com.harmony.umbrella.log;

/**
 * 日志消息
 * 
 * @author wuxii@foxmail.com
 */
public interface Message {

    /**
     * 日志格式化前的消息
     * 
     * @return 格式化前的日志消息
     */
    String getFormat();

    /**
     * 格式化后的日志消息
     * 
     * @return 格式化后的日志消息
     */
    String getFormattedMessage();

    /**
     * 格式化模版数据
     * 
     * @return 日志模版数据
     */
    Object[] getParameters();

    /**
     * 日志中的异常内容
     * 
     * @return 异常内容
     */
    Throwable getThrowable();

}

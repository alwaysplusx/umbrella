package com.harmony.umbrella.log;

/**
 * 异常处理工具
 * 
 * @author wuxii@foxmail.com
 */
public interface ProblemHandler {

    /**
     * 异常问题自定义处理
     * 
     * @param exception
     *            自定义处理异常
     * @param info
     *            异常的日志信息
     */
    void handle(Throwable problem, LogInfo info);

}

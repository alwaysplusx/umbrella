package com.harmony.umbrella.log;

import java.lang.reflect.Method;

/**
 * 异常处理工具
 * 
 * @author wuxii@foxmail.com
 */
public interface ErrorHandler {

    /**
     * 处理异常
     * 
     * @param logInfo
     *            异常的消息
     * @param method
     *            出异常的方法
     * @param target
     *            出异常的对象
     */
    void handle(LogInfo logInfo, Method method, Object target);

}

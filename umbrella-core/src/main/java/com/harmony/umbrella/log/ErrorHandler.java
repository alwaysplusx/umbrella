package com.harmony.umbrella.log;

import java.lang.reflect.Method;

/**
 * @author wuxii@foxmail.com
 */
public interface ErrorHandler {

    void handle(LogInfo logInfo, Method method, Object target);

}

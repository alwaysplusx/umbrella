package com.harmony.umbrella.log;

import java.lang.reflect.Method;

/**
 * @author wuxii@foxmail.com
 */
public class ErrorHandlerManager {

    public static final ErrorHandlerManager INSTANCE = new ErrorHandlerManager();

    public void dispatch(LogInfo logInfo, Method method, Object target, Class<? extends ErrorHandler> errorHandlerClass) {
        if (!errorHandlerClass.isInterface()) {
            try {
                ErrorHandler handler = errorHandlerClass.newInstance();
                handler.handle(logInfo, method, target);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

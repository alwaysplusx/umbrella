package com.harmony.umbrella.log.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.harmony.umbrella.log.ErrorHandler;
import com.harmony.umbrella.log.Level.StandardLevel;

@Target({ METHOD })
@Retention(RUNTIME)
public @interface Logging {

    /**
     * 模块
     */
    String module() default "";

    /**
     * 操作名称
     */
    String action() default "";

    /**
     * key expression
     */
    String key() default "";

    /**
     * 日志类型，可分为业务日志与系统日志(默认为业务日志)
     * 
     * @return
     */
    LogType type() default LogType.OPERATION;

    /**
     * 日志消息
     * <p>
     * 可以通过模版的方式对消息日志进行装配
     */
    String message() default "";

    /**
     * 日志级别
     */
    StandardLevel level() default StandardLevel.INFO;

    /**
     * 异常处理
     */
    Class<? extends ErrorHandler> errorHandler() default ErrorHandler.class;

    public enum LogType {
        SYSTEM, OPERATION
    }
}

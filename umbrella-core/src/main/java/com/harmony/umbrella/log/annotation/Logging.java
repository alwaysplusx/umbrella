package com.harmony.umbrella.log.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.harmony.umbrella.el.CheckedResolver;
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
     * 业务模块
     */
    String bizModule() default "";

    /**
     * 操作名称
     */
    String action() default "";

    /**
     * 日志级别
     */
    StandardLevel level() default StandardLevel.INFO;

    /**
     * id所在的位置或名称
     */
    String id() default "";

    /**
     * 日志消息
     * <p>
     * 可以通过模版的方式对消息日志进行装配
     */
    String message() default "";

    /**
     * 异常处理
     */
    Class<? extends ErrorHandler> errorHandler() default ErrorHandler.class;

    /**
     * 自定义表达式支持
     */
    Class<? extends CheckedResolver<?>>[] resolvers() default {};

}

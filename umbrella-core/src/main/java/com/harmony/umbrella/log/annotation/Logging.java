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
     * key表达式对应的阶段表达式
     * 
     * @return scope
     */
    Scope keyScope() default Scope.IN;

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

    /**
     * message中scope为in的expression表达式
     * 
     * @return in阶段的expression
     */
    String[] inProperties() default {};

    /**
     * 在response阶段的属性，message中设置有expression。expression才通常情况下问方法请求阶段绑定值，
     * 对于http中的请求，有可能需要获取方法完成后request中的值。即设置在outProperties中的表达式表示为在方法完成阶段所对应的值
     * 
     * @return 返回阶段的expression
     */
    String[] outProperties() default {};

    /**
     * 日志类型
     * 
     * @author wuxii@foxmail.com
     */
    public enum LogType {
        /**
         * 表示系统日志
         */
        SYSTEM, //
        /**
         * 表示操作日志
         */
        OPERATION
    }
}

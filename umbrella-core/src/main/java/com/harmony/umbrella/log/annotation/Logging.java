package com.harmony.umbrella.log.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.harmony.umbrella.log.Level.StandardLevel;

/**
 * 注解化的日志api
 * 
 * @author wuxii@foxmail.com
 */
@Target({ METHOD })
@Retention(RUNTIME)
public @interface Logging {

    /**
     * 模块
     * 
     * @return 模块
     */
    String module() default "";

    /**
     * 操作名称
     * 
     * @return 操作名称
     */
    String action() default "";

    /**
     * 业务关键key的表达式
     * 
     * @return key
     */
    String key() default "";

    /**
     * 提供负责的key表达式配置, 虽然keyExpression的表达式为数组,但是只解析数组中的第一个表达式
     * 
     * @return key表达式
     */
    Expression[] keyExpression() default {};

    /**
     * 日志消息
     * <p>
     * 可以通过模版的方式对消息日志进行装配
     * 
     * @return 消息内容
     */
    String message() default "";

    /**
     * 匹配message中的表达式,增加message中的表达式的可配置项
     * 
     * @return expression annotation
     */
    Expression[] expressions() default {};

    /**
     * 是否为系统日志，默认为false
     * 
     * @return logType
     */
    LogType type() default LogType.OPERATION;

    /**
     * 日志级别
     * 
     * @return 日志级别
     */
    StandardLevel level() default StandardLevel.INFO;

    // FIXME properties属性添加
    // Expression[] properties() default {};

    /**
     * 异常处理
     * 
     * @return 异常处理
     */
    // FIXME 添加errorHandler功能
    // Class<? extends ErrorHandler> errorHandler() default ErrorHandler.class;

    @Retention(RUNTIME)
    @Target(ANNOTATION_TYPE)
    public @interface Expression {

        /**
         * 表达式值, 优先级name > value
         * 
         * @return 表达式
         */
        String value() default "";

        /**
         * 表达式值, 优先级比较value要大
         * 
         * @return 表达式值
         */
        String name() default "";

        /**
         * 表达式所代表的值所属的scope, default is Scope.OUT
         * 
         * @return scope
         */
        Scope scope() default Scope.OUT;

        /**
         * 表达式的切割符号
         * 
         * @return delimiter
         */
        String delimiter() default "";

        /**
         * 通过index与message中的表达式匹配
         * 
         * @return index
         */
        int index() default Integer.MIN_VALUE;

    }

    /**
     * 日志类型: 分为系统日志,业务日志
     * 
     * @author wuxii@foxmail.com
     */
    public enum LogType {
        /**
         * 系统日志
         */
        SYSTEM,
        /**
         * 业务日志
         */
        OPERATION
    }

    public enum Scope {
        /**
         * 请求阶段
         */
        IN,
        /**
         * 应答阶段
         */
        OUT;
    }

}

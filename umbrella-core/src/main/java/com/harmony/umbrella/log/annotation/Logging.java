package com.harmony.umbrella.log.annotation;

import com.harmony.umbrella.log.Level;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 注解化的日志api
 *
 * @author wuxii@foxmail.com
 */
@Inherited
@Target({METHOD, TYPE})
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
     * key的获取的域
     *
     * @return key scope
     */
    Scope keyScope() default Scope.IN;

    /**
     * 日志消息
     * <p>
     * 可以通过模版的方式对消息日志进行装配
     *
     * @return 消息内容
     */
    String message() default "";

    /**
     * 于message中的表达式的相关绑定, 增加message中的表达式的可配置项
     *
     * @return expression annotation
     */
    Expression[] binds() default {};

    /**
     * 日志级别
     *
     * @return 日志级别
     */
    Level level() default Level.INFO;

    /**
     * 日志表达式中的表达式详细配置注解
     *
     * @author wuxii@foxmail.com
     */
    @Retention(RUNTIME)
    @Target(ANNOTATION_TYPE)
    @interface Expression {

        /**
         * 与表达式中绑定的值
         *
         * @return 绑定的文本值
         */
        String bind() default "";

        /**
         * 表达式值, 优先级text > value
         *
         * @return 表达式
         */
        @AliasFor("text")
        String value() default "";

        /**
         * 表达式值, 优先级比较value要大
         *
         * @return 表达式值
         */
        @AliasFor("value")
        String text() default "";

        /**
         * 表达式所代表的值所属的scope, default is Scope.OUT
         *
         * @return scope
         */
        Scope scope() default Scope.OUT;

    }

    enum Scope {
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

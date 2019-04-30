package com.harmony.umbrella.log.annotation;

import com.harmony.umbrella.log.Level;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
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
     * key的获取时段
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
    @AliasFor("value")
    String message() default "";

    /**
     * alias for message
     *
     * @return alias for message
     */
    @AliasFor("message")
    String value() default "";

    /**
     * 于message中的表达式的相关绑定, 增加message中的表达式的可配置项
     *
     * @return expression annotation
     */
    Binding[] bindings() default {};

    /**
     * 日志级别
     *
     * @return 日志级别
     */
    Level level() default Level.INFO;

}

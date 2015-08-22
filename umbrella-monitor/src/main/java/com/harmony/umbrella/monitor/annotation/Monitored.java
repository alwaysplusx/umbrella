package com.harmony.umbrella.monitor.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 用于标注方法或类， 表示该方法或类需要被监控
 * 
 * @author wuxii@foxmail.com
 */
@Inherited
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
public @interface Monitored {

    /**
     * 模块名称
     */
    String module() default "";

    /**
     * 操作类型
     */
    String operator() default "";

    /**
     * 对应日志的级别
     */
    Level level() default Level.INFO;

    /**
     * 代理对象的内部参数获取工具
     */
    InternalProperty[] internalProperties() default {};

    /**
     * http监控获取request中的信息
     */
    HttpProperty[] httpProperties() default {};

    /**
     * 日志级别
     * 
     * @author wuxii@foxmail.com
     */
    enum Level {
        TRACE, INFO, WARN, ERROR
    }

}
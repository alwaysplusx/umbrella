package com.harmony.umbrella.monitor.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

import com.harmony.umbrella.monitor.annotation.Monitored.Level;

/**
 * 此注解配合CDI使用. /META-INF/beans.xml
 */
@Inherited
@Monitored
@InterceptorBinding
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface MonitoredInterceptor {

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
}

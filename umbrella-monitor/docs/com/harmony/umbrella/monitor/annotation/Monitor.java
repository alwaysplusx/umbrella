package com.harmony.umbrella.monitor.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.harmony.umbrella.monitor.Monitor.MonitorPolicy;

/**
 * 用于标注方法或类， 表示该方法或类需要被监控
 * 
 * @author wuxii@foxmail.com
 */
@Inherited
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
public @interface Monitor {

    /**
     * 模块名称
     */
    String name() default "";

    MonitorPolicy policy() default MonitorPolicy.All;

    /**
     * 代理对象的内部参数获取工具
     */
    InternalProperty[] internalProperties() default {};

    /**
     * http监控获取request中的信息
     */
    HttpProperty[] httpProperties() default {};

}
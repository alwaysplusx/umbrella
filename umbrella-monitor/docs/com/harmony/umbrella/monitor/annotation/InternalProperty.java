package com.harmony.umbrella.monitor.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.harmony.umbrella.monitor.Attacker;

/**
 * 获取代理对象的内部数据
 * 
 * @author wuxii@foxmail.com
 */
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
public @interface InternalProperty {

    /**
     * 监控对象内容处理工具类
     */
    Class<? extends Attacker<?>> attacker();

    /**
     * 监控的内部属性或无参get方法的名称
     */
    String[] names() default {};

    /**
     * 何时拦截
     */
    Mode mode() default Mode.INOUT;
}
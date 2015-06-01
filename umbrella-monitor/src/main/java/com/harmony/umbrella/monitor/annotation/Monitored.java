package com.harmony.umbrella.monitor.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

import com.harmony.umbrella.monitor.InnerAttacker;

/**
 * 此注解配合CDI使用. /META-INF/beans.xml
 */
@Inherited
@InterceptorBinding
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
public @interface Monitored {

    /**
     * 模块名称
     * 
     * @return
     */
    String module() default "";

    /**
     * 操作类型
     * 
     * @return
     */
    String operator() default "";

    /**
     * 对监控对象内部数据的获取工具
     * 
     * @return
     */
    Attacker[] attacker() default {};

    @Target({ TYPE, METHOD })
    @Retention(RUNTIME)
    public @interface Attacker {

        /**
         * 监控对象内容处理工具类
         * 
         * @return
         */
        Class<? super InnerAttacker> attacker();

        /**
         * 监控的内部属性的名称
         * 
         * @return
         */
        String[] names() default {};

    }

}
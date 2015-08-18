package com.harmony.umbrella.monitor.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.harmony.umbrella.monitor.Attacker;

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
     * 用于支持Http监控中http中的请求参数获取
     */
    String[] requestType() default {};

    /**
     * 用于支持Http监控中http中的返回参数获取
     */
    String[] responseType() default {};

    /**
     * 对监控对象内部数据的获取工具
     */
    AttackerProperty[] assist() default {};

    /**
     * 日志级别
     * 
     * @author wuxii@foxmail.com
     */
    enum Level {
        TRACE, INFO, WARN, ERROR
    }

    @Target({ TYPE, METHOD })
    @Retention(RUNTIME)
    public @interface AttackerProperty {

        /**
         * 监控对象内容处理工具类
         */
        Class<? extends Attacker<?>> attacker();

        /**
         * 监控的内部属性或无参get方法的名称
         */
        String[] names() default {};

    }

}
package com.harmony.umbrella.monitor.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 需要监控的Http内部信息
 *
 * @author wuxii@foxmail.com
 */
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
public @interface HttpProperty {

    /**
     * http的参数作用域, 一个方法上至多配置一个scope，如果有重复的则最先配置的生效
     */
    Scope scope() default Scope.PARAMETER;

    /**
     * 需要获取的值
     */
    String[] properties();

    /**
     * 何时拦截
     */
    Mode mode() default Mode.IN;

    /**
     * 对应Http的作用域
     *
     * @author wuxii@foxmail.com
     */
    public static enum Scope {
        /**
         * 对应于
         * {@linkplain javax.servlet.http.HttpServletRequest#getParameter(String)}
         */
        PARAMETER,
        /**
         * 对应于
         * {@linkplain javax.servlet.http.HttpServletRequest#getAttribute(String)}
         */
        REQUEST,
        /**
         * 对应于{@linkplain javax.servlet.http.HttpServletRequest#getSession()}
         */
        SESSION
    }

}

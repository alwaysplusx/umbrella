package com.harmony.umbrella.log.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author wuxii@foxmail.com
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface Module {

    /**
     * 模块名称
     *
     * @return 模块名称
     */
    String value() default "";

}

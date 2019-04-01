package com.harmony.umbrella.lock.annotation;

import org.springframework.core.annotation.AliasFor;

/**
 * @author wuxii
 */
public @interface Lock {

    @AliasFor("value")
    String name() default "";

    @AliasFor("name")
    String value() default "";

    String key() default "";

    String keyGenerator() default "";

    int timeout() default 60000;

}

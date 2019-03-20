package com.harmony.umbrella.lock.annotation;

import org.springframework.core.annotation.AliasFor;

/**
 * @author wuxii
 */
public @interface Lock {

    @AliasFor("value")
    String key();

    @AliasFor("key")
    String value();

    int timeout() default 3000;

}

package com.harmony.umbrella.graphql.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface GraphqlObject {

    @AliasFor("value")
    String name() default "";

    @AliasFor("name")
    String value() default "";

}

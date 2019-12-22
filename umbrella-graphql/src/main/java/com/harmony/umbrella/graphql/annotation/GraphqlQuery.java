package com.harmony.umbrella.graphql.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 仅适用单表查?
 *
 * @author wuxin
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface GraphqlQuery {

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    String description() default "";

}

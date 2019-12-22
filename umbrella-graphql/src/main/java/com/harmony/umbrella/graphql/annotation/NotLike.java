package com.harmony.umbrella.graphql.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

import static com.harmony.umbrella.data.Operator.NOT_LIKE;

/**
 * @author wuxin
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@GraphqlParam(condition = NOT_LIKE)
public @interface NotLike {

    @AliasFor(annotation = GraphqlParam.class)
    String value() default "";

    @AliasFor(annotation = GraphqlParam.class)
    String name() default "";

}

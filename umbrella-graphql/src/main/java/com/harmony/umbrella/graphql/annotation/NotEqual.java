package com.harmony.umbrella.graphql.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

import static com.harmony.umbrella.data.Operator.NOT_EQUAL;

/**
 * @author wuxin
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@GraphqlParam(condition = NOT_EQUAL)
public @interface NotEqual {

    @AliasFor(annotation = GraphqlParam.class)
    String value() default "";

    @AliasFor(annotation = GraphqlParam.class)
    String name() default "";

}

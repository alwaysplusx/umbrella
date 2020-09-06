package com.harmony.umbrella.graphql.query.annotation;

import com.harmony.umbrella.data.Operator;
import com.harmony.umbrella.graphql.annotation.GraphqlParam;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author wuxin
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@GraphqlParam(condition = Operator.EQUAL)
public @interface Equal {

    @AliasFor(annotation = GraphqlParam.class)
    String value() default "";

    @AliasFor(annotation = GraphqlParam.class)
    String name() default "";

}

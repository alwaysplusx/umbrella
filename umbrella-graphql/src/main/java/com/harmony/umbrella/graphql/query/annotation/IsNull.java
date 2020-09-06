package com.harmony.umbrella.graphql.annotation;

import com.harmony.umbrella.data.Operator;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@GraphqlParam(condition = Operator.NULL)
public @interface IsNull {

    @AliasFor(annotation = GraphqlParam.class)
    String value() default "";

    @AliasFor(annotation = GraphqlParam.class)
    String name() default "";

}

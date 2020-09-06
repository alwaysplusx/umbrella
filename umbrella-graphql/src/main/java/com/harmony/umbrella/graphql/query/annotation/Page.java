package com.harmony.umbrella.graphql.annotation;

import com.harmony.umbrella.data.Operator;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@GraphqlParam(condition = Operator.SIZE_OF)
public @interface Page {

}

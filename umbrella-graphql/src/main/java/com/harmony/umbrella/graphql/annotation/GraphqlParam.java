package com.harmony.umbrella.graphql.annotation;

import com.harmony.umbrella.data.Operator;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author wuxin
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface GraphqlParam {

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    Operator condition() default Operator.EQUAL;


}

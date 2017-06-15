package com.harmony.umbrella.web.method.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

import com.harmony.umbrella.data.query.QueryFeature;

/**
 * @author wuxii@foxmail.com
 */
@Target({ ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface BundleQuery {

    boolean required() default false;

    QueryFeature[] feature() default { QueryFeature.CONJUNCTION };

    @AliasFor("value")
    String prefix() default "";

    @AliasFor("prefix")
    String value() default "";

    String separator() default "";

    int page() default 0;

    int size() default 20;

    String[] grouping() default {};

    String[] asc() default {};

    String[] desc() default {};

}

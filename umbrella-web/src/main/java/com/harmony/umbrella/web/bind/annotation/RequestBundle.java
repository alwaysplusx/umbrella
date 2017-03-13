package com.harmony.umbrella.web.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.harmony.umbrella.data.query.QueryFeature;

/**
 * @author wuxii@foxmail.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestBundle {

    boolean required() default false;

    QueryFeature[] feature() default { QueryFeature.CONJUNCTION };

    int page() default -1;

    int size() default -1;

    String[] gouping() default {};

    String[] asc() default {};

    String[] desc() default {};

}

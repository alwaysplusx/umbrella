package com.harmony.umbrella.graphql.annotation;

import graphql.schema.DataFetcher;

import java.lang.annotation.*;

/**
 * @author wuxin
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface GraphqlFetcher {

    String name() default "";

    Class<? extends DataFetcher> value() default DataFetcher.class;

}

package com.harmony.umbrella.graphql.annotation;

import java.lang.annotation.*;

/**
 * @author wuxin
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface GraphqlImport {

    Class<?>[] value() default {};

    Class<?>[] classes() default {};

}

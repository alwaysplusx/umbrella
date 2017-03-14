package com.harmony.umbrella.web.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @author wuxii@foxmail.com
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Serialization {

    PatternConverter converter() default PatternConverter.AUTO;

    String[] excludes() default {};

    String[] includes() default {};

    SerializerFeature[] features() default {};

    Class<? extends SerializeFilter>[] filters() default {};

    boolean camelCase() default false;

    boolean fetchLazy() default true;

    boolean safeFetch() default true;

    boolean simplePage() default true;

}

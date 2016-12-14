package com.harmony.umbrella.json.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wuxii@foxmail.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface JsonGroup {

    /**
     * 将需要序列化的字段进行分组
     * 
     * @return 分组条件
     */
    Class[] value() default {};

}

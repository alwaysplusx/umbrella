package com.harmony.umbrella.ws.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识服务端主键,通过主键标识可以唯一确定一条业务数据
 * 
 * @author wuxii@foxmail.com
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Key {

    /**
     * key的名称
     * 
     * @return 服务端的key名称
     */
    String name() default "";

    /**
     * 返回位置的排序信息
     * 
     * @return 排序值
     */
    int ordinal() default 0;

}

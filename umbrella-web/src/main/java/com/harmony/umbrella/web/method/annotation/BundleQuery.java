package com.harmony.umbrella.web.method.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

import com.harmony.umbrella.data.QueryFeature;

/**
 * web查询请求的配置注解, 可配置项:
 * <ul>
 * <li>查询featuer
 * <li>查询参数 & 参数分割符
 * <li>分页信息
 * <li>分组信息
 * <li>排序信息
 * </ul>
 * 
 * @author wuxii@foxmail.com
 */
@Target({ ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface BundleQuery {

    /**
     * 查询的特性
     * 
     * @return query feature
     */
    QueryFeature[] feature() default { QueryFeature.CONJUNCTION };

    /**
     * web parameter key的前缀
     * 
     * @return prefix
     */
    @AliasFor("value")
    String prefix() default "";

    /**
     * alias for prefix
     * 
     * @return prefix
     */
    @AliasFor("prefix")
    String value() default "";

    /**
     * web parameter key的分割符
     * 
     * @return 分割符
     */
    String separator() default "";

    /**
     * web请求的分页页码
     * 
     * @return 页码
     */
    int page() default 0;

    /**
     * web分页请求的分页size
     * 
     * @return 分页size
     */
    int size() default 20;

    /**
     * web请求中数据的分组参数
     * 
     * @return grouping
     */
    String[] grouping() default {};

    /**
     * asc排序参数名称
     * 
     * @return asc排序参数
     */
    String[] asc() default {};

    /**
     * desc排序参数
     * 
     * @return desc排序参数
     */
    String[] desc() default {};

}

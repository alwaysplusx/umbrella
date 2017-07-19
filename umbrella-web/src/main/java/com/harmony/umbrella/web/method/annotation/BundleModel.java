package com.harmony.umbrella.web.method.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * web请求的数据模型, form提交的数据可能存在前缀的情况.
 * <p>
 * 如: model.user = xx, model.password = yy. 这时候需要将提交的数据组合成一个user对象则需要指明前缀
 * 
 * <pre>
 * public Object save(@BundleModel("model") User user) {
 *     user.getUser();// xx
 *     user.getPassword(); //yy
 *     return null;
 * }
 * </pre>
 * 
 * @author wuxii@foxmail.com
 */
@Documented
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface BundleModel {

    /**
     * 提交的数据的前缀, alias form prefix
     * 
     * @return 前缀
     */
    @AliasFor("prefix")
    String value() default "";

    /**
     * 提交数据的前缀
     * 
     * @return 前缀
     */
    @AliasFor("value")
    String prefix() default "";

    /**
     * model名称的分隔符
     * 
     * @return 分隔符
     */
    String separator() default ".";

    /**
     * mavContainer Model中的名称
     * 
     * @return model name
     */
    String model() default "";

}

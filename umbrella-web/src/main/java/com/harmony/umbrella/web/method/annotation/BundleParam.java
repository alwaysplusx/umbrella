package com.harmony.umbrella.web.method.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

import com.harmony.umbrella.web.method.support.ModelFragment;
import com.harmony.umbrella.web.method.support.ViewFragment;

/**
 * http提交数据中ModelFragment中的form数据,
 * 提交后数据通过{@linkplain ModelFragment#getData(String)}访问
 * 
 * @author wuxii@foxmail.com
 * @see ViewFragment
 */
@Documented
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface BundleParam {

    /**
     * 需要获取的数据key
     * 
     * @return parameter keys
     */
    @AliasFor("value")
    String[] params() default {};

    /**
     * alias form params
     * 
     * @return parameter keys
     */
    @AliasFor("params")
    String[] value() default {};

}

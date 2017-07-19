package com.harmony.umbrella.web.method.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Controller;

/**
 * 自定义Bundle控制器
 * 
 * @author wuxii@foxmail.com
 */
@Documented
@Controller
@BundleView
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BundleController {

    String value() default "";

}

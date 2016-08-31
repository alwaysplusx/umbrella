package com.harmony.umbrella.log.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.harmony.umbrella.log.ProblemHandler;

/**
 * @author wuxii@foxmail.com
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface Module {

    /**
     * 模块名称
     * 
     * @return 模块名称
     */
    String value() default "";

    /**
     * 异常处理
     * 
     * @return 异常处理
     */
    Class<? extends ProblemHandler> handler() default ProblemHandler.class;

}

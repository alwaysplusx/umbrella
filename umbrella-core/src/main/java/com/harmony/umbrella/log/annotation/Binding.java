package com.harmony.umbrella.log.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * message中的表达式与实际表达式的绑定关系, 对message中不能进行具体描述的表达式进行详细描述的作用
 *
 * @author wuxii
 */
@Target({ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface Binding {

    /**
     * message中的表达式关键key
     *
     * @return key
     */
    String key();

    /**
     * 绑定的表达式
     *
     * @return 绑定的表达式
     */
    String expression();

    /**
     * 值的取值时段
     *
     * @return 取值时段
     */
    Scope scope() default Scope.IN;
}

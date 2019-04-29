package com.harmony.umbrella.log.annotation;

/**
 * @author wuxii
 */
public @interface KeyExpression {
    /**
     * 与表达式中绑定的值
     *
     * @return 绑定的文本值
     */
    String bind() default "";

    /**
     * 表达式值
     *
     * @return 表达式值
     */
    String text();

    /**
     * 表达式所代表的值所属的scope
     *
     * @return scope
     */
    Scope scope();
}

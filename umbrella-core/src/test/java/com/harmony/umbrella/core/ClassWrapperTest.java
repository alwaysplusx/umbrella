package com.harmony.umbrella.core;

import com.harmony.umbrella.log.expression.ExpressionResolver;

/**
 * @author wuxii@foxmail.com
 */
public class ClassWrapperTest {

    public static void main(String[] args) {
        ClassWrapper<ExpressionResolver> cw = new ClassWrapper<ExpressionResolver>(ExpressionResolver.class);
        Class<?>[] classes = cw.getAllSubClasses();
        for (Class<?> clazz : classes) {
            System.out.println(clazz.getName());
        }
    }

}

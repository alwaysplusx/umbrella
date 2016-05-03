package com.harmony.umbrella.core;

import com.harmony.umbrella.el.TypedResolver;

/**
 * @author wuxii@foxmail.com
 */
public class ClassWrapperTest {

    public static void main(String[] args) {
        ClassWrapper<TypedResolver> cw = new ClassWrapper<TypedResolver>(TypedResolver.class);
        Class<?>[] classes = cw.getAllSubClasses();
        for (Class<?> clazz : classes) {
            System.out.println(clazz.getName());
        }
    }

}

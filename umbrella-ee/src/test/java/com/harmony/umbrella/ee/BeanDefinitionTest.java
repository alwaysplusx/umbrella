package com.harmony.umbrella.ee;

import java.util.Arrays;

/**
 * @author wuxii@foxmail.com
 */
public class BeanDefinitionTest {

    public static void main(String[] args) {
        BeanDefinition bd = new BeanDefinition(FooBean.class);
        System.out.println(Arrays.asList(bd.getRemoteClasses()));
        System.out.println(Arrays.asList(bd.getLocalClasses()));
    }

}

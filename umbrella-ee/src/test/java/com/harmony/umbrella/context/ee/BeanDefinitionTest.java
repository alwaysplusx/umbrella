package com.harmony.umbrella.context.ee;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
@SuppressWarnings("rawtypes")
public class BeanDefinitionTest {

    @Test
    public void test() {
        BeanDefinition bd = new BeanDefinition(TestRemote.class);
        assertEquals(TestRemote.class, bd.getBeanClass());
        for (Class clazz : bd.getAllRemoteClasses()) {
            System.out.println(clazz.getName());
        }
        for (Class clazz : bd.getAllLocalClasses()) {
            System.out.println(clazz.getName());
        }
    }

    @Test
    public void testRemote() {
        BeanDefinition bd = new BeanDefinition(TestBean.class);
        assertEquals(TestBean.class, bd.getBeanClass());
        assertEquals(TestRemote.class, bd.getRemoteClass());
        assertEquals(TestLocal.class, bd.getLocalClass());
    }

}

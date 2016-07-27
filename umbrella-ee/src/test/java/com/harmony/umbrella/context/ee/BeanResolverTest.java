package com.harmony.umbrella.context.ee;

import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.umbrella.context.ee.support.ConfigurationBeanResolver;

/**
 * @author wuxii@foxmail.com
 */
public class BeanResolverTest {

    private static BeanResolver beanResolver;

    @BeforeClass
    public static void beforeClass() {
        beanResolver = new ConfigurationBeanResolver();
    }

    @Test
    public void guessNamesTest() {
        String[] names = beanResolver.guessNames(BeanResolver.class);
        for (String name : names) {
            System.out.println(name);
        }
    }

}

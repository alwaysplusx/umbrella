package com.harmony.umbrella.context.ee;

import com.harmony.umbrella.context.ee.support.ConfigurationBeanResolver;

/**
 * @author wuxii@foxmail.com
 */
public class ConfigurationBeanResolverTest {

    public static void main(String[] args) {
        ConfigurationBeanResolver resolver = new ConfigurationBeanResolver();
        String[] guessNames = resolver.guessNames(new BeanDefinition(TestRemote.class), null);
        for (String name : guessNames) {
            System.out.println(name);
        }
    }

}

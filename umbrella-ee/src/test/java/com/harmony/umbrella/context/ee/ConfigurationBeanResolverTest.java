package com.harmony.umbrella.context.ee;

import java.io.IOException;
import java.util.Properties;

import com.harmony.umbrella.context.ee.resolver.ConfigurationBeanResolver;
import com.harmony.umbrella.util.PropUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ConfigurationBeanResolverTest {

    public static void main(String[] args) throws IOException {
        Properties properties = PropUtils.loadProperties("META-INF/application.properties");
        ConfigurationBeanResolver resolver = ConfigurationBeanResolver.create(properties);
        String[] names = resolver.guessNames(new BeanDefinition(TestRemote.class));
        for (String name : names) {
            System.out.println(name);
        }
    }

}

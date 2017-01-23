package com.harmony.umbrella.test;

import java.util.Properties;

/**
 * @author wuxii@foxmail.com
 */
public class TestUtils {

    public static Properties toProperties(Property[] properties) {
        Properties props = new Properties();
        for (Property property : properties) {
            props.setProperty(property.name(), property.value());
        }
        return props;
    }

}

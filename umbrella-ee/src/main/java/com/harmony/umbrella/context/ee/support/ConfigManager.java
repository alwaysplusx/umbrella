package com.harmony.umbrella.context.ee.support;

import java.util.Set;

/**
 * @author wuxii@foxmail.com
 */
public interface ConfigManager {

    String getJndi(Class<?> clazz);

    void setJndi(Class<?> clazz, String jndi);

    String removeJndi(Class<?> clazz);

    Set<String> getPropertySet(String key);

    Set<String> getPropertySet(String key, Set<String> def);

    String getProperty(String key);

    String getProperty(String key, String def);

    void setProperty(String key, String value);

}

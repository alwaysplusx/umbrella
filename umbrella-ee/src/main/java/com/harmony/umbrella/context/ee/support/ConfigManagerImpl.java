package com.harmony.umbrella.context.ee.support;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wuxii@foxmail.com
 */
public class ConfigManagerImpl implements ConfigManager {

    protected final Properties properties;

    @SuppressWarnings("rawtypes")
    protected final Map<Class, String> jndiMap = new ConcurrentHashMap<Class, String>();

    private String delimiter = ",";

    public ConfigManagerImpl(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String getJndi(Class<?> clazz) {
        return jndiMap.get(clazz);
    }

    @Override
    public void setJndi(Class<?> clazz, String jndi) {
        jndiMap.put(clazz, jndi);
    }

    @Override
    public String removeJndi(Class<?> clazz) {
        return jndiMap.remove(clazz);
    }

    @Override
    public Set<String> getPropertySet(String key) {
        String property = properties.getProperty(key);
        return property == null ? new HashSet<String>() : asSet(property, delimiter);
    }

    @Override
    public Set<String> getPropertySet(String key, Set<String> def) {
        String property = properties.getProperty(key);
        return property == null ? def : asSet(property, delimiter);
    }

    @Override
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    @Override
    public String getProperty(String key, String def) {
        return properties.getProperty(key, def);
    }

    @Override
    public void setProperty(String key, String value) {
        this.properties.setProperty(key, value);
    }

    protected Set<String> asSet(String name, String regex) {
        StringTokenizer st = new StringTokenizer(name, regex);
        Set<String> result = new HashSet<String>(st.countTokens());
        while (st.hasMoreTokens()) {
            result.add(st.nextToken().trim());
        }
        return result;
    }

    public void setDelimier(String delimiter) {
        this.delimiter = delimiter;
    }

}

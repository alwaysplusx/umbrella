package com.harmony.umbrella.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.harmony.umbrella.util.PropertiesUtils;

/**
 * 基于配置文件的配置管理基础实现
 * 
 * @author wuxii@foxmail.com
 */
public class SimplePropertyManager extends AbstractPropertyManager {

    @SuppressWarnings("rawtypes")
    private Map properties = new HashMap();

    public SimplePropertyManager() {
    }

    public SimplePropertyManager(String fileLocation) throws IOException {
        this.setPropertiesFile(fileLocation);
    }

    public SimplePropertyManager(Properties properties) {
        this.properties = new HashMap<>(properties);
    }

    public SimplePropertyManager(Map<?, ?> properties) {
        this.properties = new HashMap<>(properties);
    }

    @Override
    public PropertyEntry get(String key) {
        Object value = properties.get(key);
        return value == null ? null : new PropertyEntry(key, value.toString());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Property> getStartWith(String prefix) {
        List<Property> params = new ArrayList<Property>();
        Map<String, ?> p = PropertiesUtils.filterStartWith(prefix, properties);
        for (String name : p.keySet()) {
            PropertyEntry param = get(name);
            if (param != null) {
                params.add(param);
            }
        }
        return params;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set(Property param) {
        properties.put(param.getKey(), param.getValue());
    }

    @SuppressWarnings("rawtypes")
    public Map getProperties() {
        return properties;
    }

    @SuppressWarnings("rawtypes")
    public void setProperties(Map properties) {
        this.properties = properties;
    }

    public void setPropertiesFile(String fileLocation) throws IOException {
        this.properties = PropertiesUtils.loadProperties(fileLocation);
    }

}

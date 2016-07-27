package com.harmony.umbrella.config;

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
public class PropertyParamManager extends AbstractParamManager {

    @SuppressWarnings("rawtypes")
    private Map properties = new HashMap();

    public PropertyParamManager() {
    }

    public PropertyParamManager(String fileLocation) throws IOException {
        this.setPropertiesFile(fileLocation);
    }

    public PropertyParamManager(Properties properties) {
        this.properties = properties;
    }

    public PropertyParamManager(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public DefaultParam get(String key) {
        Object value = properties.get(key);
        return value == null ? null : new DefaultParam(key, value.toString());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Param> getStartWith(String prefix) {
        List<Param> params = new ArrayList<Param>();
        Map<String, ?> p = PropertiesUtils.filterStartWith(prefix, properties);
        for (String name : p.keySet()) {
            DefaultParam param = get(name);
            if (param != null) {
                params.add(param);
            }
        }
        return params;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set(Param param) {
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

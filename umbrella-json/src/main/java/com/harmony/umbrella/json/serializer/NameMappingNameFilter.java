package com.harmony.umbrella.json.serializer;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.serializer.NameFilter;

/**
 * @author wuxii@foxmail.com
 */
public class NameMappingNameFilter implements NameFilter {

    private final Map<Class, Map<String, String>> nameMappings;

    public NameMappingNameFilter() {
        this(new HashMap<>());
    }

    public NameMappingNameFilter(Map<Class, Map<String, String>> nameMappings) {
        this.nameMappings = nameMappings;
    }

    @Override
    public String process(Object object, String name, Object value) {
        Class<?> clazz = object.getClass();
        String mappingName = getMappingName(clazz, name);
        return mappingName == null ? name : mappingName;
    }

    protected String getMappingName(Class<?> type, String name) {
        Map<String, String> mapping = getTypeMapping(type);
        return mapping != null ? mapping.get(name) : null;
    }

    public Map<String, String> getTypeMapping(Class<?> type) {
        return nameMappings.get(type);
    }

    public Map<Class, Map<String, String>> getNameMappings() {
        return nameMappings;
    }

    public void setNameMappings(Map<Class, Map<String, String>> nameMappings) {
        this.nameMappings.clear();
        this.nameMappings.putAll(nameMappings);
    }

    public void addTypeMapping(Class<?> type, Map<String, String> mapping) {
        Map<String, String> m = getTypeMapping(type);
        if (m == null) {
            this.nameMappings.put(type, mapping);
        } else {
            m.putAll(mapping);
        }
    }

    public void setTypeMapping(Class<?> type, Map<String, String> mapping) {
        this.nameMappings.put(type, mapping);
    }

}

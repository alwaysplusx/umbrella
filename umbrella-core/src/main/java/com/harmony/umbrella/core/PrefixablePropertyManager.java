package com.harmony.umbrella.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.harmony.umbrella.util.PropertiesUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class PrefixablePropertyManager extends AbstractPropertyManager {

    private Map<String, Object> properties;

    private String prefix;

    private boolean defaultIsBlank;

    private boolean fetchWithoutPrefix = true;

    private boolean setWithoutPrefix;

    public PrefixablePropertyManager() {
    }

    public PrefixablePropertyManager(String prefix, Map<String, Object> properties) {
        this.prefix = prefix;
        this.properties = properties;
    }

    @Override
    public PropertyEntry get(String key) {
        String fullKey = getFullKey(key);
        Object value = properties.get(fullKey);
        return value == null ? null : new PropertyEntry(fetchWithoutPrefix ? key : fullKey, value);
    }

    @Override
    public List<Property> getStartWith(final String prefix) {
        String fullPrefix = getFullKey(prefix);
        String selfPrefix = fullPrefix.substring(0, fullPrefix.length() - prefix.length());
        List<Property> result = new ArrayList<Property>();
        Map<String, ?> p = PropertiesUtils.filterStartWith(fullPrefix, properties);
        for (Entry<String, ?> e : p.entrySet()) {
            String key = e.getKey();
            if (fetchWithoutPrefix) {
                key = key.substring(selfPrefix.length());
            }
            result.add(new PropertyEntry(key, e.getValue()));
        }
        return result;
    }

    @Override
    public void set(Property param) {
        String key = param.getKey();
        if (!setWithoutPrefix) {
            key = getFullKey(key);
        }
        properties.put(key, param.getValue());
    }

    public String getFullKey(String key) {
        if (StringUtils.isBlank(prefix) || (defaultIsBlank && "default".equals(prefix))) {
            return key;
        }
        return prefix + "." + key;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isDefaultIsBlank() {
        return defaultIsBlank;
    }

    public void setDefaultIsBlank(boolean defaultIsBlank) {
        this.defaultIsBlank = defaultIsBlank;
    }

    public boolean isFetchWithoutPrefix() {
        return fetchWithoutPrefix;
    }

    public void setFetchWithoutPrefix(boolean fetchWithoutPrefix) {
        this.fetchWithoutPrefix = fetchWithoutPrefix;
    }

    public boolean isSetWithoutPrefix() {
        return setWithoutPrefix;
    }

    public void setSetWithoutPrefix(boolean setWithoutPrefix) {
        this.setWithoutPrefix = setWithoutPrefix;
    }

}

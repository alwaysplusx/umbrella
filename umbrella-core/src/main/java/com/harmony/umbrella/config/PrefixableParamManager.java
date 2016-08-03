package com.harmony.umbrella.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.metadata.ServerMetadata;
import com.harmony.umbrella.util.PropertiesUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class PrefixableParamManager extends AbstractParamManager {

    @SuppressWarnings("rawtypes")
    private Map properties;

    private String prefix;

    private boolean defaultIsBlank;

    private boolean containerPrefix;

    private boolean fetchWithoutPrefix;

    public PrefixableParamManager() {
    }

    @SuppressWarnings("rawtypes")
    public PrefixableParamManager(Map properties) {
        this.setProperties(properties);
    }

    @Override
    public Param get(String key) {
        String fullKey = getFullKey(key);
        Object value = properties.get(fullKey);
        return value == null ? null : new ParamEntry(key, value.toString());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set(Param param) {
        String key = param.getKey();
        properties.put(getFullKey(key), param.getObject());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Param> getStartWith(final String prefix) {
        String fullPrefix = getFullKey(prefix);
        String selfPrefix = fullPrefix.substring(0, fullPrefix.length() - prefix.length());
        List<Param> result = new ArrayList<Param>();
        Map<String, ?> p = PropertiesUtils.filterStartWith(fullPrefix, properties);
        for (Entry<String, ?> e : p.entrySet()) {
            String key = e.getKey();
            if (fetchWithoutPrefix) {
                key = key.substring(selfPrefix.length());
            }
            result.add(new ParamEntry(key, e.getValue()));
        }
        return result;
    }

    public String getFullKey(String key) {
        if (StringUtils.isBlank(prefix) || (defaultIsBlank && "default".equals(prefix))) {
            return key;
        }
        if ("$container$".equals(prefix)) {
            ServerMetadata metadata = ApplicationContext.getServerMetadata();
            if (metadata.serverType != ServerMetadata.UNKNOW) {
                return metadata.serverName + "." + key;
            }
        }
        return prefix + "." + key;
    }

    @SuppressWarnings("rawtypes")
    public Map getProperties() {
        return properties;
    }

    @SuppressWarnings("rawtypes")
    public void setProperties(Map properties) {
        this.properties = properties;
        Object p = properties.get("prefix");
        if (p != null && p instanceof String) {
            this.prefix = (String) p;
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isContainerPrefix() {
        return containerPrefix;
    }

    public void setContainerPrefix(boolean containerPrefix) {
        this.containerPrefix = containerPrefix;
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

    public void setPropertiesFile(String fileLocation) throws IOException {
        this.setProperties(PropertiesUtils.loadProperties(fileLocation));
    }

}

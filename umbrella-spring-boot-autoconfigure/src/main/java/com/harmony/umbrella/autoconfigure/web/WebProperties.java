package com.harmony.umbrella.autoconfigure.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.json.KeyStyle;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ConfigurationProperties(prefix = "harmony.web")
public class WebProperties {

    private String ipHeader;
    private CurrentFilter currentFilter = new CurrentFilter();
    private View view = new View();
    private boolean bundle;
    private Fastjson fastjson = new Fastjson();

    public WebProperties() {
    }

    public Fastjson getFastjson() {
        return fastjson;
    }

    public void setFastjson(Fastjson fastjson) {
        this.fastjson = fastjson;
    }

    public String getIpHeader() {
        return ipHeader;
    }

    public void setIpHeader(String ipHeader) {
        this.ipHeader = ipHeader;
    }

    public CurrentFilter getCurrentFilter() {
        return currentFilter;
    }

    public void setCurrentFilter(CurrentFilter currentFilter) {
        this.currentFilter = currentFilter;
    }

    public boolean isBundle() {
        return bundle;
    }

    public void setBundle(boolean bundle) {
        this.bundle = bundle;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public static class View {

        private KeyStyle keyStyle;

        private String dateFormat;

        private int defaultSerializerFeatures = JSON.DEFAULT_GENERATE_FEATURE;

        private Set<String> globalExcludeFields = new HashSet<>();

        private Set<String> globalIncludeFields = new HashSet<>();

        public KeyStyle getKeyStyle() {
            return keyStyle;
        }

        public void setKeyStyle(KeyStyle keyStyle) {
            this.keyStyle = keyStyle;
        }

        public Set<String> getGlobalExcludeFields() {
            return globalExcludeFields;
        }

        public void setGlobalExcludeFields(Set<String> globalExcludeFields) {
            this.globalExcludeFields = globalExcludeFields;
        }

        public Set<String> getGlobalIncludeFields() {
            return globalIncludeFields;
        }

        public void setGlobalIncludeFields(Set<String> globalIncludeFields) {
            this.globalIncludeFields = globalIncludeFields;
        }

        public String getDateFormat() {
            return dateFormat;
        }

        public void setDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
        }

        public int getDefaultSerializerFeatures() {
            return defaultSerializerFeatures;
        }

        public void setDefaultSerializerFeatures(int defaultSerializerFeatures) {
            this.defaultSerializerFeatures = defaultSerializerFeatures;
        }
    }

    public static class CurrentFilter {

        private boolean enabled;
        private Set<String> urlPatterns = new HashSet<>();
        private Set<String> excludes = new HashSet<>();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Set<String> getUrlPatterns() {
            return urlPatterns;
        }

        public void setUrlPatterns(Set<String> urlPatterns) {
            this.urlPatterns = urlPatterns;
        }

        public Set<String> getExcludes() {
            return excludes;
        }

        public void setExcludes(Set<String> excludes) {
            this.excludes = excludes;
        }

    }

    public static class Fastjson {

        private boolean enabled;
        private String charset = "utf-8";
        private PropertyNamingStrategy propertyNamingStrategy = PropertyNamingStrategy.CamelCase;
        private List<SerializerFeature> serializerFeatures = new ArrayList<>();
        private String dateFormat;
        private boolean writeContentLength = true;

        public boolean isEnabled() {
            return enabled;
        }

        public Fastjson setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public PropertyNamingStrategy getPropertyNamingStrategy() {
            return propertyNamingStrategy;
        }

        public Fastjson setPropertyNamingStrategy(PropertyNamingStrategy propertyNamingStrategy) {
            this.propertyNamingStrategy = propertyNamingStrategy;
            return this;
        }

        public String getCharset() {
            return charset;
        }

        public Fastjson setCharset(String charset) {
            this.charset = charset;
            return this;
        }

        public List<SerializerFeature> getSerializerFeatures() {
            return serializerFeatures;
        }

        public Fastjson setSerializerFeatures(List<SerializerFeature> serializerFeatures) {
            this.serializerFeatures = serializerFeatures;
            return this;
        }

        public String getDateFormat() {
            return dateFormat;
        }

        public Fastjson setDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
            return this;
        }

        public boolean isWriteContentLength() {
            return writeContentLength;
        }

        public Fastjson setWriteContentLength(boolean writeContentLength) {
            this.writeContentLength = writeContentLength;
            return this;
        }
    }

}
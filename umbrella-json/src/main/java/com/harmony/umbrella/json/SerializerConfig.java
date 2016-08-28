package com.harmony.umbrella.json;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.json.serializer.FilterMode;
import com.harmony.umbrella.json.serializer.SimpleMemberAnnotationFilter;
import com.harmony.umbrella.json.serializer.SimplePropertyNameFilter;

/**
 * 
 * @author wuxii@foxmail.com
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class SerializerConfig<T extends SerializerConfig> {

    protected final Set<String> filterProperties = new LinkedHashSet<String>();

    protected final Set<SerializerFeature> features = new HashSet<SerializerFeature>();

    protected final List<SerializeFilter> filters = new ArrayList<SerializeFilter>();

    protected final Set<Class<? extends Annotation>> annotationClasses = new HashSet<Class<? extends Annotation>>();

    protected SerializeConfig config;

    protected FilterMode propertyFilterMode;

    protected FilterMode annationFilterMode;

    public T withPropertyFilterMode(FilterMode mode) {
        this.propertyFilterMode = mode;
        return (T) this;
    }

    public T withFilterProperty(FilterMode mode, String... property) {
        this.propertyFilterMode = mode;
        Collections.addAll(this.filterProperties, property);
        return (T) this;
    }

    public T withFilterProperty(String... property) {
        Collections.addAll(this.filterProperties, property);
        return (T) this;
    }

    public T withAnnotationFilterModel(FilterMode mode) {
        this.annationFilterMode = mode;
        return (T) this;
    }

    public T withFilterAnnotation(Class<? extends Annotation>... annCls) {
        Collections.addAll(this.annotationClasses, annCls);
        return (T) this;
    }

    public T withFilterAnnotation(FilterMode mode, Class<? extends Annotation>... annCls) {
        this.annationFilterMode = mode;
        Collections.addAll(this.annotationClasses, annCls);
        return (T) this;
    }

    public T withSerializerFeature(SerializerFeature... feature) {
        Collections.addAll(this.features, feature);
        return (T) this;
    }

    public T withSerializeFilter(SerializeFilter... filter) {
        Collections.addAll(this.filters, filter);
        return (T) this;
    }

    public T withSerializeConfig(SerializeConfig config) {
        this.config = config;
        return (T) this;
    }

    public final SerializeFilter[] getFilters() {
        List<SerializeFilter> result = new ArrayList<SerializeFilter>();
        SerializeFilter propertyFilter = getPropertyFilter();

        if (propertyFilter != null) {
            result.add(propertyFilter);
        }

        SerializeFilter annFilter = getAnnotationFilter();
        if (annFilter != null) {
            result.add(annFilter);
        }

        SerializeFilter[] subFilters = buildFilters();
        if (subFilters != null && subFilters.length > 0) {
            Collections.addAll(result, subFilters);
        }

        result.addAll(filters);
        return result.toArray(new SerializeFilter[0]);
    }

    /**
     * 采用子类收集的配置来创建SerializeFilter
     * 
     * @return 子类配置的filter
     */
    protected SerializeFilter[] buildFilters() {
        return null;
    }

    public final SerializerFeature[] getFeatures() {
        return features.toArray(new SerializerFeature[0]);
    }

    public final SerializeConfig getSerializeConfig() {
        return config;
    }

    public final SerializeFilter getPropertyFilter() {
        if (filterProperties.isEmpty()) {
            return null;
        }
        return new SimplePropertyNameFilter(filterProperties, propertyFilterMode == null ? FilterMode.EXCLUDE : propertyFilterMode);
    }

    public final SerializeFilter getAnnotationFilter() {
        if (annotationClasses.isEmpty()) {
            return null;
        }
        return new SimpleMemberAnnotationFilter(annotationClasses, annationFilterMode == null ? FilterMode.EXCLUDE : annationFilterMode);
    }
}
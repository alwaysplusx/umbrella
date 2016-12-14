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
import com.harmony.umbrella.json.serializer.SimpleAnnotationFilter;
import com.harmony.umbrella.json.serializer.SimplePropertyNameFilter;

/**
 * 序列化配置项
 * 
 * @author wuxii@foxmail.com
 */
public class SerializerConfig<T extends SerializerConfig> {

    /**
     * 与{@linkplain #propertyFilterMode}结合生成对于属性的过滤
     */
    protected final Set<String> filterProperties = new LinkedHashSet<String>();

    /**
     * 序列化特性
     */
    protected final Set<SerializerFeature> features = new HashSet<SerializerFeature>();

    /**
     * 自定义序列化filter
     */
    protected final List<SerializeFilter> filters = new ArrayList<SerializeFilter>();

    /**
     * 与{@linkplain #annationFilterMode}结合生成对特定注解的过滤
     */
    protected final Set<Class<? extends Annotation>> annotationClasses = new HashSet<Class<? extends Annotation>>();

    /**
     * fastjson的序列化配置
     */
    protected SerializeConfig config;

    /**
     * 属性过滤mode, default {@linkplain FilterMode#EXCLUDE}
     */
    protected FilterMode propertyFilterMode;

    /**
     * 注解过滤mode, default {@linkplain FilterMode#EXCLUDE}
     */
    protected FilterMode annationFilterMode;

    /**
     * 设置属性过滤的模式
     * 
     * @param mode
     *            过滤模式
     * @return this
     */
    public T withPropertyFilterMode(FilterMode mode) {
        this.propertyFilterMode = mode;
        return (T) this;
    }

    /**
     * 设置过滤的属性以及模式
     * 
     * @param mode
     *            过滤模式
     * @param property
     *            过滤的属性
     * @return this
     */
    public T withFilterProperty(FilterMode mode, String... property) {
        this.propertyFilterMode = mode;
        Collections.addAll(this.filterProperties, property);
        return (T) this;
    }

    /**
     * 设置过滤的属性
     * 
     * @param property
     *            过滤的属性
     * @return this
     */
    public T withFilterProperty(String... property) {
        Collections.addAll(this.filterProperties, property);
        return (T) this;
    }

    /**
     * 设置字段注解的过滤模式
     * 
     * @param mode
     *            过滤模式
     * @return this
     */
    public T withAnnotationFilterModel(FilterMode mode) {
        this.annationFilterMode = mode;
        return (T) this;
    }

    /**
     * 设置过滤的属性
     * 
     * @param annCls
     *            过滤的属性
     * @return this
     */
    public T withFilterAnnotation(Class<? extends Annotation>... annCls) {
        Collections.addAll(this.annotationClasses, annCls);
        return (T) this;
    }

    /**
     * 设置过滤的注解以及模式
     * 
     * @param mode
     *            模式
     * @param annCls
     *            注解
     * @return
     */
    public T withFilterAnnotation(FilterMode mode, Class<? extends Annotation>... annCls) {
        this.annationFilterMode = mode;
        Collections.addAll(this.annotationClasses, annCls);
        return (T) this;
    }

    /**
     * 设置序列化的特性
     * 
     * @param feature
     *            特性
     * @return this
     */
    public T withSerializerFeature(SerializerFeature... feature) {
        Collections.addAll(this.features, feature);
        return (T) this;
    }

    /**
     * 自定义的序列化filter
     * 
     * @param filter
     *            自定义filter
     * @return this
     */
    public T withSerializeFilter(SerializeFilter... filter) {
        Collections.addAll(this.filters, filter);
        return (T) this;
    }

    /**
     * 设置fastjson的过滤特性
     * 
     * @param config
     *            fastjson序列化特性
     * @return this
     */
    public T withSerializeConfig(SerializeConfig config) {
        this.config = config;
        return (T) this;
    }

    /**
     * 集合所有配置生成过滤器
     * 
     * @return 过滤器
     */
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

    /**
     * 序列化特性
     * 
     * @return
     */
    public final SerializerFeature[] getFeatures() {
        return features.toArray(new SerializerFeature[features.size()]);
    }

    /**
     * fastjson序列化配置
     * 
     * @return fastjson序列化配置
     */
    public final SerializeConfig getSerializeConfig() {
        return config;
    }

    /**
     * 生成对属性的过滤filter
     * 
     * @return property filter
     */
    public final SerializeFilter getPropertyFilter() {
        if (filterProperties.isEmpty()) {
            return null;
        }
        return new SimplePropertyNameFilter(filterProperties, propertyFilterMode == null ? FilterMode.EXCLUDE : propertyFilterMode);
    }

    /**
     * 生成对注解的filter
     * 
     * @return annotation filter
     */
    public final SerializeFilter getAnnotationFilter() {
        if (annotationClasses.isEmpty()) {
            return null;
        }
        return new SimpleAnnotationFilter(annotationClasses, annationFilterMode == null ? FilterMode.EXCLUDE : annationFilterMode);
    }
}
package com.harmony.umbrella.json.builder;

import static com.harmony.umbrella.json.serializer.LazyAttributeFilter.*;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.json.PropertyTransformer;
import com.harmony.umbrella.json.SerializerConfig;
import com.harmony.umbrella.json.serializer.FilterMode;
import com.harmony.umbrella.json.serializer.LazyAttributeFilter;
import com.harmony.umbrella.json.serializer.SimpleAnnotationFilter;
import com.harmony.umbrella.json.serializer.SimplePatternFilter;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

/**
 * 整体序列化配置集成环境
 * 
 * @author wuxii@foxmail.com
 */
public class SerializerConfigBuilderFactory {

    private static final Log log = Logs.getLog(SerializerConfigBuilder.class);

    private static final Class[] LAZY_ANNOTATIONS = LAZY_ANNOTATION_CLASSES.toArray(new Class[LAZY_ANNOTATION_CLASSES.size()]);

    private List<PropertyTransformer> propertyTransformers = new ArrayList<>();

    public SerializerConfigBuilderFactory() {
    }

    public SerializerConfigBuilderFactory(PropertyTransformer... tsfs) {
        this.propertyTransformers.addAll(Arrays.asList(tsfs));
    }

    /**
     * 为指定的类创建序列化配置
     * 
     * @param clazz
     *            需要配置的类
     * @return 序列化配置
     */
    public SerializerConfigBuilder configFor(Class<?> clazz) {
        return new SerializerConfigBuilder(clazz);
    }

    /**
     * 为指定的类创建序列化配置
     * 
     * @param clazz
     *            需要配置的类
     * @param defaultFilterMode
     *            配置默认的过滤模式
     * @return 序列化配置
     */
    public SerializerConfigBuilder configFor(Class<?> clazz, FilterMode defaultFilterMode) {
        return new SerializerConfigBuilder(clazz, defaultFilterMode);
    }

    /**
     * 添加类型属性转化器
     * 
     * @param tsfs
     *            类型属性转化器
     */
    public final void addTypedPropertyTransformers(PropertyTransformer... tsfs) {
        for (PropertyTransformer tsf : tsfs) {
            this.propertyTransformers.add((PropertyTransformer) tsf.clone());
        }
    }

    /**
     * 获取类型属性转化器, 其获取的转化器为其内部持有的拷贝
     * 
     * @return 类型属性转化器
     */
    public final PropertyTransformer[] getTypedPropertyTransformers(Class<?> type) {
        List<PropertyTransformer> result = new ArrayList<>();
        for (int i = 0; i < propertyTransformers.size(); i++) {
            PropertyTransformer tsf = propertyTransformers.get(i);
            if (tsf.support(type)) {
                result.add((PropertyTransformer) tsf.clone());
            }
        }
        return result.toArray(new PropertyTransformer[result.size()]);
    }

    /**
     * 检测是否有指定的类型属性转化器
     * 
     * @param type
     *            检测的类型
     * @return true or false
     */
    public boolean hasTypedPropertyTransformer(Class<?> type) {
        for (PropertyTransformer tsf : propertyTransformers) {
            if (tsf.support(type)) {
                return true;
            }
        }
        return false;
    }

    private String[] transformProperty(Class<?> type, String... property) {
        Set<String> result = new LinkedHashSet<>();
        if (type != null && property.length > 0) {
            for (PropertyTransformer tf : propertyTransformers) {
                if (tf.support(type)) {
                    result.addAll(tf.transform(type, property));
                }
            }
            if (result.isEmpty()) {
                log.warn("{} not have typed property transformer, just return source property", type);
            }
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * 序列化配置类
     * 
     * @author wuxii@foxmail.com
     */
    public class SerializerConfigBuilder<T extends SerializerConfigBuilder<T>> {

        /**
         * 当前配置的所指定type
         */
        private final Class<?> type;

        /**
         * 默认配置的过滤模式
         */
        private final FilterMode defaultMode;

        /**
         * 排除的属性集合
         */
        private Set<String> excludeProperties = new LinkedHashSet<String>();

        /**
         * 包含的属性集合
         */
        private Set<String> includeProperties = new LinkedHashSet<String>();

        /**
         * 排除的直接属性
         */
        private Map<Class, Set<String>> excludeAttributeMap = new HashMap<>();

        /**
         * 包含的直接属性
         */
        private Map<Class, Set<String>> includeAttributeMap = new HashMap<>();

        /**
         * 包含的注解集合
         */
        private Set<Class<? extends Annotation>> includeAnnCls = new LinkedHashSet<>();

        /**
         * 排除的注解集合
         */
        private Set<Class<? extends Annotation>> excludeAnnCls = new LinkedHashSet<>();

        /**
         * 序列化特性
         */
        private Set<SerializerFeature> features = new HashSet<>();

        /**
         * 序列化过滤器
         */
        private List<SerializeFilter> filters = new ArrayList<>();

        /**
         * fastjson序列化配置
         */
        private SerializeConfig serializeConfig;

        /**
         * 在lazyFilter是否主动尝试加载lazy属性的标志位
         */
        private boolean lazyFilterTryFetch;

        /**
         * lazy filter中需要过滤的注解
         */
        private Set<Class<? extends Annotation>> lazyAnnCls = new LinkedHashSet<>();

        protected SerializerConfigBuilder(Class<?> type) {
            this(type, FilterMode.EXCLUDE);
        }

        protected SerializerConfigBuilder(Class<?> type, FilterMode defaultMode) {
            this.type = type;
            this.defaultMode = defaultMode;
        }

        /**
         * 使用默认的过滤模式{@linkplain #defaultMode}过滤属性
         * 
         * @param property
         *            待过滤的属性
         * @return this
         */
        public T withProperty(String... property) {
            return withProperty(defaultMode, property);
        }

        /**
         * 使用指定的过滤模式过滤属性
         * 
         * @param mode
         *            过滤模式
         * @param property
         *            带过滤的属性
         * @return this
         */
        public T withProperty(FilterMode mode, String... property) {
            (mode == FilterMode.INCLUDE ? includeProperties : excludeProperties).addAll(Arrays.asList(property));
            return (T) this;
        }

        /**
         * 对当前的属性进行属性转化后形成过滤的属性, 其过滤模式为默认的过滤模式
         * 
         * @param property
         *            需要被转化的过滤属性
         * @return this
         */
        public T withTypeProperty(String... property) {
            return withProperty(defaultMode, property);
        }

        /**
         * 对当前的属性进行转化后再对其进行指定的过滤模式过滤
         * 
         * @param mode
         *            过滤模式
         * @param property
         *            需要被转化的过滤属性
         * @return this
         */
        public T withTypeProperty(FilterMode mode, String... property) {
            return withProperty(mode, transformProperty(type, property));
        }

        /**
         * 过滤指定类型下的直接属性
         * 
         * @param type
         *            指定的类型
         * @param attrs
         *            直接属性
         * @return this
         */
        public T withAttribute(Class<?> type, String... attrs) {
            return withAttribute(type, defaultMode, attrs);
        }

        /**
         * 按指定的过滤模式过滤类型下的直接属性
         * 
         * @param type
         *            指定的类型
         * @param mode
         *            过滤模式
         * @param attrs
         *            直接属性
         * @return this
         */
        public T withAttribute(Class<?> type, FilterMode mode, String... attrs) {
            Set<String> attributeSet = getTypeAttributeSet(type, mode);
            attributeSet.addAll(Arrays.asList(attrs));
            return (T) this;
        }

        protected Set<String> getTypeAttributeSet(Class<?> type, FilterMode mode) {
            Map<Class, Set<String>> attrMap = (mode == FilterMode.INCLUDE ? includeAttributeMap : excludeAttributeMap);
            Set<String> result = attrMap.get(type);
            if (result == null) {
                result = new LinkedHashSet<>();
                attrMap.put(type, result);
            }
            return result;
        }

        /**
         * 使用默认过滤模式过滤带有输入注解的attribute
         * 
         * @param annCls
         *            需要过滤的注解
         * @return this
         */
        public T withAnnotationClass(Class<? extends Annotation>... annCls) {
            return withAnnotationClass(defaultMode, annCls);
        }

        /**
         * 使用指定的过滤模式来过滤带有特定注解的attribute
         * 
         * @param mode
         *            过滤模式
         * @param annCls
         *            需要过滤的注解
         * @return this
         */
        public T withAnnotationClass(FilterMode mode, Class<? extends Annotation>... annCls) {
            (mode == FilterMode.INCLUDE ? includeAnnCls : excludeAnnCls).addAll(Arrays.asList(annCls));
            return (T) this;
        }

        /**
         * 添加lazy filter, 并启用默认的过滤属性与模式
         * 过滤{@linkplain LazyAttributeFilter#LAZY_ANNOTATION_CLASSES}中的所有注解,
         * 不启用其tryFetch特性
         * 
         * @return this
         */
        public T withDefaultLazyFilter() {
            return (T) withLazyFilter(false, LAZY_ANNOTATIONS);
        }

        /**
         * 弃用lazy filter, 原设置的lazyFilter属性也一并清空
         * 
         * @return this
         */
        public T withoutLazyFilter() {
            this.lazyFilterTryFetch = false;
            this.lazyAnnCls.clear();
            return (T) this;
        }

        /**
         * 启用lazy filter并启用其tryFetch特性
         * 
         * @param annCls
         *            lazy filter需要过滤的注解
         * @return this
         */
        public T withLazyFilterAndTryFetch(Class<? extends Annotation>... annCls) {
            return withLazyFilter(true, annCls);
        }

        /**
         * 启用lazy filter但不启用其tryFetch特性
         * 
         * @param annCls
         *            lazy filter需要过滤的注解
         * @return this
         */
        public T withLazyFilterAndNotTryFetch(Class<? extends Annotation>... annCls) {
            return withLazyFilter(false, annCls);
        }

        /**
         * 启用lazy filter
         * 
         * @param tryFetch
         *            是否启用lazy filter的tryFetch特性
         * @param annCls
         *            需要被过滤的注解
         * @return this
         */
        public T withLazyFilter(boolean tryFetch, Class<? extends Annotation>... annCls) {
            this.lazyFilterTryFetch = tryFetch;
            this.lazyAnnCls.addAll(Arrays.asList(annCls));
            return (T) this;
        }

        /**
         * 添加序列化特性
         * 
         * @param feature
         *            序列化特性
         * @return this
         */
        public T withFeature(SerializerFeature... feature) {
            this.features.addAll(Arrays.asList(feature));
            return (T) this;
        }

        /**
         * 添加序列化过滤器
         * 
         * @param filter
         *            序列化过滤器
         * @return this
         */
        public T withFilter(SerializeFilter... filter) {
            this.filters.addAll(Arrays.asList(filter));
            return (T) this;
        }

        /**
         * 添加fastjson序列化配置
         * 
         * @param config
         *            fastjson序列化配置
         * @return this
         */
        public T withSerializeConfig(SerializeConfig config) {
            this.serializeConfig = config;
            return (T) this;
        }

        public SerializerConfig build() {
            List<SerializeFilter> filters = new ArrayList<>();
            if (!excludeProperties.isEmpty()) {
                filters.add(new SimplePatternFilter(excludeProperties, FilterMode.EXCLUDE));
            }
            if (!includeProperties.isEmpty()) {
                filters.add(new SimplePatternFilter(includeProperties, FilterMode.INCLUDE));
            }
            if (!excludeAnnCls.isEmpty()) {
                filters.add(new SimpleAnnotationFilter(excludeAnnCls, FilterMode.EXCLUDE));
            }
            if (!includeAnnCls.isEmpty()) {
                filters.add(new SimpleAnnotationFilter(includeAnnCls, FilterMode.INCLUDE));
            }
            if (!lazyAnnCls.isEmpty()) {
                filters.add(new LazyAttributeFilter(lazyFilterTryFetch, lazyAnnCls));
            }
            filters.addAll(this.filters);
            return new SerializerConfigImpl(//
                    filters.toArray(new SerializeFilter[filters.size()]), // 
                    features.toArray(new SerializerFeature[features.size()]), //
                    serializeConfig//
            );
        }

    }

    private static class SerializerConfigImpl implements SerializerConfig {

        private final SerializeFilter[] filters;
        private final SerializerFeature[] features;
        private SerializeConfig config;

        public SerializerConfigImpl(SerializeFilter[] filters, SerializerFeature[] features, SerializeConfig config) {
            this.filters = filters;
            this.features = features;
            this.config = config;
        }

        @Override
        public SerializeFilter[] getFilters() {
            return filters;
        }

        @Override
        public SerializerFeature[] getFeatures() {
            return features;
        }

        @Override
        public SerializeConfig getSerializeConfig() {
            return config;
        }

    }

}

package com.harmony.umbrella.data.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.data.domain.BaseEntity;
import com.harmony.umbrella.json.SerializerConfig;
import com.harmony.umbrella.json.serializer.FilterMode;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

/**
 * 整体序列化配置集成环境
 * 
 * @author wuxii@foxmail.com
 */
public class SerializerConfigFactory {

    private static final Log log = Logs.getLog(DataSerializerConfig.class);

    private static final String[] ENTITY_FIELD_NAMES;

    static {
        Field[] fields = BaseEntity.class.getDeclaredFields();
        ENTITY_FIELD_NAMES = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            ENTITY_FIELD_NAMES[i] = fields[i].getName();
        }
    }

    private List<TypedPropertyTransformer> typedPropertyTransformers = new ArrayList<>();

    public DataSerializerConfig configFor(Class<?> clazz) {
        return new DataSerializerConfig(clazz);
    }

    public DataSerializerConfig configFor(Class<?> clazz, FilterMode defaultFilterMode) {
        return new DataSerializerConfig(clazz, defaultFilterMode);
    }

    public TypedPropertyTransformer[] getTypedPropertyTransformers() {
        return typedPropertyTransformers.toArray(new TypedPropertyTransformer[typedPropertyTransformers.size()]);
    }

    public boolean hasTypedPropertyTransformer(Class<?> type) {
        TypedPropertyTransformer[] tfs = getTypedPropertyTransformers();
        for (TypedPropertyTransformer tf : tfs) {
            if (tf.support(type)) {
                return true;
            }
        }
        return false;
    }

    private String[] transformProperty(Class<?> type, String... property) {
        if (type != null && property.length > 0) {
            TypedPropertyTransformer[] tfs = getTypedPropertyTransformers();
            for (TypedPropertyTransformer tf : tfs) {
                if (tf.support(type)) {
                    return tf.transform(property);
                }
            }
            log.warn("{} not have typed property transformer, just return source property", type);
        }
        return property;
    }

    public class DataSerializerConfig implements SerializerConfig<DataSerializerConfig> {

        private final Class<?> type;

        private final FilterMode defaultMode;

        private Set<String> excludeProperties = new LinkedHashSet<String>();

        private Set<String> includeProperties = new LinkedHashSet<String>();

        private Set<Class<? extends Annotation>> includeAnnCls = new LinkedHashSet<>();

        private Set<Class<? extends Annotation>> excludeAnnCls = new LinkedHashSet<>();

        private FilterMode entityFilterMode;

        private Set<String> entityProperties = new LinkedHashSet<>();

        private FilterMode persistableFilterMode;

        private Set<String> persistableProperties = new LinkedHashSet<>();

        private Set<SerializerFeature> features = new HashSet<>();

        private List<SerializeFilter> filters = new ArrayList<>();

        private SerializeConfig serializeConfig;

        private boolean addLazyFilter;

        private boolean lazyFilterTryFetch;

        public DataSerializerConfig(Class<?> type) {
            this(type, FilterMode.EXCLUDE);
        }

        public DataSerializerConfig(Class<?> type, FilterMode defaultMode) {
            this.type = type;
            this.defaultMode = defaultMode;
        }

        public DataSerializerConfig withProperty(String... property) {
            return withProperty(defaultMode, property);
        }

        public DataSerializerConfig withProperty(FilterMode mode, String... property) {
            (mode == FilterMode.INCLUDE ? includeProperties : excludeProperties).addAll(Arrays.asList(property));
            return this;
        }

        public DataSerializerConfig withTypedProperty(String... property) {
            return withProperty(defaultMode, property);
        }

        public DataSerializerConfig withTypedProperty(FilterMode mode, String... property) {
            return withProperty(mode, transformProperty(type, property));
        }

        public DataSerializerConfig withAnnotationClass(Class<? extends Annotation>... annCls) {
            return withAnnotationClass(defaultMode, annCls);
        }

        public DataSerializerConfig withAnnotationClass(FilterMode mode, Class<? extends Annotation>... annCls) {
            (mode == FilterMode.INCLUDE ? includeAnnCls : excludeAnnCls).addAll(Arrays.asList(annCls));
            return this;
        }

        public DataSerializerConfig withAllPersistableProperty(FilterMode mode) {
            return withPersistableProperty(mode, "id", "new");
        }

        public DataSerializerConfig withPersistableProperty(FilterMode mode, String... property) {
            this.persistableFilterMode = mode;
            this.persistableProperties.addAll(Arrays.asList(property));
            return this;
        }

        public DataSerializerConfig withAllEntityProperty(FilterMode mode) {
            return withEntityProperty(mode, ENTITY_FIELD_NAMES);
        }

        public DataSerializerConfig withEntityProperty(FilterMode mode, String... property) {
            this.entityFilterMode = mode;
            this.entityProperties.addAll(Arrays.asList(property));
            return this;
        }

        public DataSerializerConfig withoutLazyFilter() {
            this.addLazyFilter = false;
            return this;
        }

        public DataSerializerConfig withLazyFilterAndTryFetch() {
            this.addLazyFilter = true;
            this.lazyFilterTryFetch = true;
            return this;
        }

        public DataSerializerConfig withLazyFilterNotTryFetch() {
            this.addLazyFilter = true;
            this.lazyFilterTryFetch = false;
            return this;
        }

        @Override
        public DataSerializerConfig withFeature(SerializerFeature... feature) {
            this.features.addAll(Arrays.asList(feature));
            return this;
        }

        @Override
        public DataSerializerConfig withFilter(SerializeFilter... filter) {
            this.filters.addAll(Arrays.asList(filter));
            return this;
        }

        @Override
        public DataSerializerConfig withSerializeConfig(SerializeConfig config) {
            this.serializeConfig = config;
            return this;
        }

        @Override
        public Class<?> getType() {
            return type;
        }

        @Override
        public SerializeFilter[] getFilters() {
            if (!excludeProperties.isEmpty()) {

            }
            if (!includeProperties.isEmpty()) {

            }
            if (!excludeAnnCls.isEmpty()) {

            }
            if (!includeAnnCls.isEmpty()) {

            }
            if (!persistableProperties.isEmpty()) {

            }
            if (!entityProperties.isEmpty()) {

            }
            return null;
        }

        @Override
        public SerializerFeature[] getFeatures() {
            return features.toArray(new SerializerFeature[features.size()]);
        }

        @Override
        public SerializeConfig getSerializeConfig() {
            return serializeConfig;
        }

    }

}

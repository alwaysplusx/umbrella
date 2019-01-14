package com.harmony.umbrella.json;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.json.serializer.JpaMappingPropertyPreFilter;
import com.harmony.umbrella.json.serializer.MemberAnnotationPropertyPreFilter;
import com.harmony.umbrella.json.serializer.NameMappingNameFilter;
import com.harmony.umbrella.json.serializer.SimplePatternPropertyPreFilter;
import com.harmony.umbrella.util.PatternResourceFilter;
import org.springframework.util.AntPathMatcher;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * 序列化配置类
 *
 * @author wuxii@foxmail.com
 */
public class SerializerConfigBuilder {

    public static SerializerConfigBuilder newBuilder() {
        return new SerializerConfigBuilder();
    }

    /**
     * 序列化特性
     */
    private Set<SerializerFeature> features;

    /**
     * 序列化过滤器
     */
    private List<SerializeFilter> filters;

    /**
     * fastjson序列化配置
     */
    private SerializeConfig fastjsonSerializeConfig = SerializeConfig.getGlobalInstance();

    private PatternResourceFilter<String> resourceFilter;

    private MemberAnnotationPropertyPreFilter memberAnnotationPropertyFilter;

    private KeyStyle keyStyle;

    private boolean jpaSupport;

    private boolean jpaPositive;

    private NameMappingNameFilter nameMappingNameFilter;

    private String dateFormat;

    private int defaultSerializerFeature;

    protected SerializerConfigBuilder() {
    }

    public SerializerConfigBuilder setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        return this;
    }

    public SerializerConfigBuilder setDefaultSerializerFeature(int defaultSerializerFeature) {
        this.defaultSerializerFeature = defaultSerializerFeature;
        return this;
    }

    /**
     * 添加序列化特性
     *
     * @param features 序列化特性
     * @return this
     */
    public SerializerConfigBuilder addFeatures(SerializerFeature... features) {
        return addFeatures(Arrays.asList(features));
    }

    public SerializerConfigBuilder addFeatures(Collection<SerializerFeature> features) {
        if (this.features == null) {
            this.features = new HashSet<>();
        }
        this.features.addAll(features);
        return this;
    }

    /**
     * 添加序列化过滤器
     *
     * @param filters 序列化过滤器
     * @return this
     */
    public SerializerConfigBuilder addFilters(SerializeFilter... filters) {
        return addFilters(Arrays.asList(filters));
    }

    public SerializerConfigBuilder addFilters(Collection<SerializeFilter> filters) {
        if (this.filters == null) {
            this.filters = new ArrayList<>();
        }
        this.filters.addAll(filters);
        return this;
    }

    public SerializerConfigBuilder setFastjsonSerializeConfig(SerializeConfig config) {
        this.fastjsonSerializeConfig = config;
        return this;
    }

    /**
     * 使用指定的过滤模式过滤属性
     *
     * @param patterns 带过滤的属性
     * @return this builder
     */
    public SerializerConfigBuilder setIncludePatterns(Collection<String> patterns) {
        getResourceFilter().setIncludes(patterns);
        return this;
    }

    public SerializerConfigBuilder addIncludePatterns(Collection<String> patterns) {
        getResourceFilter().addIncludes(patterns);
        return this;
    }

    public SerializerConfigBuilder addIncludePatterns(String... patterns) {
        getResourceFilter().addIncludes(patterns);
        return this;
    }

    public SerializerConfigBuilder setExcludePatterns(Collection<String> patterns) {
        getResourceFilter().setExcludes(patterns);
        return this;
    }

    public SerializerConfigBuilder addExcludePatterns(Collection<String> patterns) {
        getResourceFilter().addExcludes(patterns);
        return this;
    }

    public SerializerConfigBuilder addExcludePatterns(String... patterns) {
        getResourceFilter().addExcludes(patterns);
        return this;
    }

    /**
     * 使用指定的过滤模式来过滤带有特定注解的attribute
     *
     * @param annClasses 需要过滤的注解
     * @return this
     */
    public SerializerConfigBuilder setIncludeAnnotations(Collection<Class<? extends Annotation>> annClasses) {
        getMemberAnnotationPropertyFilter().setIncludes(annClasses);
        return this;
    }

    public SerializerConfigBuilder addIncludeAnnotations(Class<? extends Annotation>... annClasses) {
        getMemberAnnotationPropertyFilter().addIncludes(annClasses);
        return this;
    }

    public SerializerConfigBuilder setExcludeAnnotations(Collection<Class<? extends Annotation>> annClasses) {
        getMemberAnnotationPropertyFilter().setExcludes(annClasses);
        return this;
    }

    public SerializerConfigBuilder addExcludeAnnotations(Class<? extends Annotation>... annClasses) {
        getMemberAnnotationPropertyFilter().addExcludes(annClasses);
        return this;
    }

    public NameMappingBuilder setNameMapping(Class<?> type) {
        return new NameMappingBuilder(type);
    }

    public SerializerConfigBuilder addTypeNameMapping(Class<?> type, String name, String mapped) {
        getTypeMapping(type).put(name, mapped);
        return this;
    }

    private Map<String, String> getTypeMapping(Class<?> type) {
        NameMappingNameFilter nmnf = getNameMappingNameFilter();
        Map<String, String> mapping = nmnf.getTypeMapping(type);
        if (mapping == null) {
            mapping = new HashMap<>();
            nmnf.setTypeMapping(type, mapping);
        }
        return mapping;
    }

    /**
     * 设置序列化的key格式, 如: key=accessToken
     * <ul>
     * <li>0 - default: 根据类的字段名默认呈现. accessToken
     * <li>1 - camelCase: accessToken
     * <li>2 - PascalCase: AccessToken
     * <li>3 - 下划线: access_token
     * </ul>
     *
     * @param style key style
     * @return this builder
     */
    public SerializerConfigBuilder setKeyStyle(KeyStyle style) {
        this.keyStyle = style;
        return this;
    }

    public SerializerConfigBuilder setJpaSupport(boolean jpaSupport) {
        return setJpaSupport(jpaSupport, true);
    }

    public SerializerConfigBuilder setJpaSupport(boolean jpaSupport, boolean positive) {
        this.jpaSupport = jpaSupport;
        this.jpaPositive = positive;
        return this;
    }

    public String toJson(Object obj) {
        return Json.toJson(obj, build());
    }

    public SerializerConfig build() {
        final List<SerializeFilter> filters = new ArrayList<>();
        if (resourceFilter != null) {
            filters.add(new SimplePatternPropertyPreFilter(resourceFilter));
        }
        if (memberAnnotationPropertyFilter != null) {
            filters.add(memberAnnotationPropertyFilter);
        }
        if (this.filters != null) {
            filters.addAll(this.filters);
        }
        if (jpaSupport) {
            filters.add(new JpaMappingPropertyPreFilter(jpaPositive));
        }
        if (nameMappingNameFilter != null && !nameMappingNameFilter.getNameMappings().isEmpty()) {
            filters.add(nameMappingNameFilter);
        }
        if (keyStyle != null && keyStyle.namingStrategy() != null) {
            fastjsonSerializeConfig.setPropertyNamingStrategy(keyStyle.namingStrategy());
        } else if (keyStyle != null) {
            filters.add(keyStyle);
        }
        return new SerializerConfigImpl(filters, features, fastjsonSerializeConfig, dateFormat, defaultSerializerFeature);
    }

    private PatternResourceFilter<String> getResourceFilter() {
        if (resourceFilter == null) {
            resourceFilter = new PatternResourceFilter<>(new AntPathMatcher("."));
        }
        return resourceFilter;
    }

    private MemberAnnotationPropertyPreFilter getMemberAnnotationPropertyFilter() {
        if (this.memberAnnotationPropertyFilter == null) {
            this.memberAnnotationPropertyFilter = new MemberAnnotationPropertyPreFilter();
        }
        return memberAnnotationPropertyFilter;
    }

    private NameMappingNameFilter getNameMappingNameFilter() {
        if (this.nameMappingNameFilter == null) {
            this.nameMappingNameFilter = new NameMappingNameFilter();
        }
        return nameMappingNameFilter;
    }

    public class NameMappingBuilder {

        private final Class<?> type;
        private Map<String, String> mappings;

        public NameMappingBuilder(Class<?> type) {
            this.type = type;
        }

        public NameMappingBuilder addMapping(String name, String mapped) {
            getMappings().put(name, mapped);
            return this;
        }

        private Map<String, String> getMappings() {
            if (mappings == null) {
                mappings = getTypeMapping(type);
            }
            return mappings;
        }

        public SerializerConfigBuilder up() {
            return SerializerConfigBuilder.this;
        }

    }

    private static class SerializerConfigImpl implements SerializerConfig {

        private SerializeFilter[] filters;
        private SerializerFeature[] features;
        private String dateFormat;
        private SerializeConfig fastjsonConfig;
        private int defaultSerializerFeature;

        private SerializerConfigImpl(Collection<SerializeFilter> filters, Collection<SerializerFeature> features,
                                     SerializeConfig fastjsonConfig, String dateFormat, int defaultSerializerFeature) {
            this.filters = filters.toArray(new SerializeFilter[0]);
            this.features = features.toArray(new SerializerFeature[0]);
            this.fastjsonConfig = fastjsonConfig;
            this.dateFormat = dateFormat;
            this.defaultSerializerFeature = defaultSerializerFeature;
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
        public SerializeConfig getFastjsonSerializeConfig() {
            return fastjsonConfig;
        }

        @Override
        public String getDateFormat() {
            return dateFormat;
        }

        @Override
        public int getDefaultSerializerFeature() {
            return defaultSerializerFeature;
        }

    }

}
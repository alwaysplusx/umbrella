package com.harmony.umbrella.json;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.core.Member;
import com.harmony.umbrella.json.serializer.CamelCaseNameFilter;
import com.harmony.umbrella.json.serializer.MemberPropertyFilter;
import com.harmony.umbrella.json.serializer.SimpleAnnotationFilter;
import com.harmony.umbrella.json.serializer.SimplePatternFilter;
import com.harmony.umbrella.util.MemberUtils;

/**
 * 序列化配置类
 * 
 * @author wuxii@foxmail.com
 */
public class SerializerConfigBuilder {

    public static SerializerConfigBuilder create() {
        return new SerializerConfigBuilder();
    }

    /**
     * 排除的属性集合
     */
    private final Set<String> excludePatterns = new LinkedHashSet<String>();

    /**
     * 包含的属性集合
     */
    private final Set<String> includePatterns = new LinkedHashSet<String>();

    /**
     * 包含的注解集合
     */
    private final Set<Class<? extends Annotation>> includeAnnCls = new LinkedHashSet<>();

    /**
     * 排除的注解集合
     */
    private final Set<Class<? extends Annotation>> excludeAnnCls = new LinkedHashSet<>();

    /**
     * 序列化特性
     */
    private final Set<SerializerFeature> features = new HashSet<>();

    /**
     * 序列化过滤器
     */
    private final List<SerializeFilter> filters = new ArrayList<>();

    /**
     * fastjson序列化配置
     */
    private SerializeConfig fastjsonSerializeConfig;

    /**
     * 在lazyFilter是否主动尝试加载lazy属性的标志位
     */
    private boolean fetchLazyAttribute = true;

    private boolean safeFetch = true;

    private boolean camelCase;

    protected SerializerConfigBuilder() {
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
    public SerializerConfigBuilder withPatterns(FilterMode mode, String... property) {
        patternSet(mode).addAll(Arrays.asList(property));
        return this;
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
    public SerializerConfigBuilder withAnnotationClass(FilterMode mode, Class<? extends Annotation>... annCls) {
        annotationSet(mode).addAll(Arrays.asList(annCls));
        return this;
    }

    /**
     * 添加序列化特性
     * 
     * @param feature
     *            序列化特性
     * @return this
     */
    public SerializerConfigBuilder withFeature(SerializerFeature... feature) {
        this.features.addAll(Arrays.asList(feature));
        return this;
    }

    /**
     * 添加序列化过滤器
     * 
     * @param filter
     *            序列化过滤器
     * @return this
     */
    public SerializerConfigBuilder withFilter(SerializeFilter... filter) {
        this.filters.addAll(Arrays.asList(filter));
        return this;
    }

    public SerializerConfigBuilder withFastjsonSerializeConfig(SerializeConfig config) {
        this.fastjsonSerializeConfig = config;
        return this;
    }

    public SerializerConfigBuilder includePatterns(String... patterns) {
        return withPatterns(FilterMode.INCLUDE, patterns);
    }

    public SerializerConfigBuilder excludePatterns(String... patterns) {
        return withPatterns(FilterMode.EXCLUDE, patterns);
    }

    public SerializerConfigBuilder fetchLazyAttribute(boolean fetchLazyAttribute) {
        this.fetchLazyAttribute = fetchLazyAttribute;
        return this;
    }

    public SerializerConfigBuilder safeFetch(boolean safeFetch) {
        this.safeFetch = safeFetch;
        return this;
    }

    public SerializerConfigBuilder camelCase(boolean camelCase) {
        this.camelCase = camelCase;
        return this;
    }

    public String toJson(Object obj) {
        return Json.toJson(obj, build());
    }

    public SerializerConfig build() {
        final List<SerializeFilter> filters = new ArrayList<>();
        if (!excludePatterns.isEmpty()) {
            filters.add(new SimplePatternFilter(new ArrayList<>(excludePatterns), FilterMode.EXCLUDE));
        }
        if (!includePatterns.isEmpty()) {
            filters.add(new SimplePatternFilter(new ArrayList<>(includePatterns), FilterMode.INCLUDE));
        }
        if (!excludeAnnCls.isEmpty()) {
            filters.add(new SimpleAnnotationFilter(new ArrayList<>(excludeAnnCls), FilterMode.EXCLUDE));
        }
        if (!includeAnnCls.isEmpty()) {
            filters.add(new SimpleAnnotationFilter(new ArrayList<>(includeAnnCls), FilterMode.INCLUDE));
        }
        if (fetchLazyAttribute) {
            filters.add(new LazyAttributeFilter(safeFetch));
        }
        if (camelCase) {
            filters.add(new CamelCaseNameFilter());
        }
        filters.addAll(this.filters);
        return new SerializerConfigImpl(//
                filters.toArray(new SerializeFilter[filters.size()]), // 
                features.toArray(new SerializerFeature[features.size()]), //
                fastjsonSerializeConfig//
        );
    }

    private Set<String> patternSet(FilterMode mode) {
        return mode == FilterMode.INCLUDE ? includePatterns : excludePatterns;
    }

    private Set<Class<? extends Annotation>> annotationSet(FilterMode mode) {
        return mode == FilterMode.INCLUDE ? includeAnnCls : excludeAnnCls;
    }

    private static final class SerializerConfigImpl implements SerializerConfig {

        private final SerializeFilter[] filters;
        private final SerializerFeature[] features;
        private SerializeConfig fastjsonConfig;

        public SerializerConfigImpl(SerializeFilter[] filters, SerializerFeature[] features, SerializeConfig fastjsonConfig) {
            this.filters = filters;
            this.features = features;
            this.fastjsonConfig = fastjsonConfig;
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

    }

    private static final class LazyAttributeFilter extends MemberPropertyFilter {

        /**
         * 需要被懒加载处理的注解
         */
        static final List<Class<? extends Annotation>> annCls;

        static {
            List<Class<? extends Annotation>> temp = new ArrayList<>();
            temp.add(ManyToOne.class);
            temp.add(ManyToMany.class);
            temp.add(OneToMany.class);
            temp.add(OneToOne.class);
            annCls = Collections.unmodifiableList(temp);
        }

        /**
         * 是否尝试通过get方法去加载lazy的属性
         */
        private boolean tryFetch;

        public LazyAttributeFilter(boolean tryFetch) {
            super(false);
            this.tryFetch = tryFetch;
        }

        @Override
        protected boolean accept(Member member, Object target) {
            FetchType fetchType = getFetchType(member);
            return fetchType == null || FetchType.EAGER.equals(fetchType) || (tryFetch && tryFetch(member, target));
        }

        public boolean tryFetch(Member member, Object object) {
            try {
                Object v = member.get(object);
                if (v == null) {
                    return true;
                } else if (v instanceof Collection) {
                    ((Collection) v).size();
                } else {
                    return tryFirstReadMethod(member.getType(), v);
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        private boolean tryFirstReadMethod(Class<?> clazz, Object object) throws Exception {
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (!ReflectionUtils.isObjectMethod(method) //
                        && Modifier.isPublic(method.getModifiers())//
                        && !Modifier.isStatic(method.getModifiers())//
                        && MemberUtils.isReadMethod(method)) {
                    ReflectionUtils.invokeMethod(method, object);
                    return true;
                }
            }
            return false;
        }

        public FetchType getFetchType(Member member) {
            Annotation ann = null;
            for (Class<? extends Annotation> annCls : annCls) {
                ann = member.getAnnotation(annCls);
                if (ann != null) {
                    break;
                }
            }
            return ann == null ? null : (FetchType) AnnotationUtils.getValue(ann, "fetch");
        }

    }
}
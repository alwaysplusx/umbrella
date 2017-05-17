package com.harmony.umbrella.web.method.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

import org.springframework.core.annotation.AliasFor;
import org.springframework.data.domain.Page;

import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.data.domain.BaseEntity;
import com.harmony.umbrella.web.method.support.ViewFragment;

/**
 * @author wuxii@foxmail.com
 */
@Target({ ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface BundleView {

    @AliasFor("excludes")
    String[] value() default {};

    /**
     * 序列化排除的字段
     * 
     * @return serialzation excludes
     */
    @AliasFor("value")
    String[] excludes() default {};

    /**
     * 只序列化的字段
     * 
     * @return serialzation includes
     */
    String[] includes() default {};

    /**
     * json序列化的特性
     * 
     * @return 序列化特性
     */
    SerializerFeature[] features() default {};

    /**
     * 序列化自定义filters
     * 
     * @return filters
     */
    Class<? extends SerializeFilter>[] filters() default {};

    /**
     * 是否序列化lazy属性
     * 
     * @return fetchLazy
     */
    boolean fetchLazy() default true;

    /**
     * 序列化的时候是否尝试lazy属性
     * 
     * @return safeFetch
     */
    boolean safeFetch() default true;

    String contentType() default "";

    boolean wrappage() default true;

    BehaviorType behavior() default BehaviorType.AUTO;

    String encoding() default "";

    Class<? extends BundleViewBehavior> behaviorClass() default BundleViewBehavior.class;

    Class<? extends PatternConverter> converter() default PatternConverter.class;

    public enum BehaviorType implements PatternConverter, BundleViewBehavior {

        AUTO {

            @Override
            public String[] convert(String[] patterns) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void apply(ViewFragment viewFragment) {
                throw new UnsupportedOperationException();
            }

        },
        PAGE {

            @Override
            public String[] convert(String[] patterns) {
                String[] result = new String[patterns.length];
                for (int i = 0; i < patterns.length; i++) {
                    String p = patterns[i];
                    if (p.startsWith("$.")) {
                        result[i] = p.substring(2);
                    } else {
                        result[i] = "records[*]." + p;
                    }
                }
                return result;
            }

            @Override
            public void apply(ViewFragment viewFragment) {
                viewFragment.excludes(PAGE, DEFAULT_EXCLUDES);
            }
        },
        ARRAY {

            @Override
            public String[] convert(String[] patterns) {
                String[] result = new String[patterns.length];
                for (int i = 0; i < patterns.length; i++) {
                    String p = patterns[i];
                    if (p.startsWith("$.")) {
                        result[i] = p.substring(2);
                    } else {
                        result[i] = "[*]." + p;
                    }
                }
                return result;
            }

            @Override
            public void apply(ViewFragment viewFragment) {
                viewFragment.excludes(ARRAY, DEFAULT_EXCLUDES);
            }
        },
        ENTITY {

            @Override
            public String[] convert(String[] patterns) {
                String[] result = new String[patterns.length];
                System.arraycopy(patterns, 0, result, 0, patterns.length);
                return result;
            }

            @Override
            public void apply(ViewFragment viewFragment) {
                viewFragment.excludes(ENTITY, DEFAULT_EXCLUDES);
            }
        },
        NONE {

            @Override
            public String[] convert(String[] patterns) {
                return BehaviorType.ENTITY.convert(patterns);
            }

            @Override
            public void apply(ViewFragment viewFragment) {
            }
        };

        private static final String[] DEFAULT_EXCLUDES = { "id", "new", "idType", "idValue", "idNames" };

        public static BehaviorType convert(Class<?> type) {
            if (Page.class.isAssignableFrom(type)) {
                return PAGE;
            } else if (Collection.class.isAssignableFrom(type) || type.isArray()) {
                return ARRAY;
            } else if (BaseEntity.class.isAssignableFrom(type)) {
                return BehaviorType.ENTITY;
            }
            return NONE;
        }
    }

    public interface PatternConverter {

        String[] convert(String[] patterns);

    }

    public interface BundleViewBehavior {

        void apply(ViewFragment viewFragment);

    }

}

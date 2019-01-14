package com.harmony.umbrella.web.method.annotation;

import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.json.KeyStyle;
import com.harmony.umbrella.json.SerializerConfigBuilder;
import com.harmony.umbrella.web.method.support.BundleViewMethodProcessor;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 配合{@linkplain BundleViewMethodProcessor}使用, 来达到注解配置式序列化返回值.
 *
 * @author wuxii@foxmail.com
 * @see BundleViewMethodProcessor
 * @see SerializerConfigBuilder
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BundleView {

    /**
     * alias for excludes
     *
     * @return 渲染view时候需要排除的字段
     */
    @AliasFor("excludes")
    String[] value() default {};

    /**
     * 渲染view时候需要排除的字段
     *
     * @return serialzation excludes
     */
    @AliasFor("value")
    String[] excludes() default {};

    /**
     * view所需要序列化的字段
     *
     * @return 所需要序列化的字段
     */
    String[] includes() default {};

    /**
     * 序列化自定义filters
     *
     * @return filters
     */
    Class<? extends SerializeFilter>[] filters() default {};

    /**
     * view进行序列化的特性
     *
     * @return 序列化特性
     */
    SerializerFeature[] features() default {};

    /**
     * key style
     *
     * @return key style
     */
    KeyStyle style() default KeyStyle.NONE;

    /**
     * 对内容类型的序列化行为
     *
     * @return 序列化内容的类型行为
     */
    Behavior behavior() default Behavior.AUTO;

    /**
     * 序列化中的{@linkplain #includes()} & {@linkplain #excludes()}中的特性
     *
     * @return
     */
    Class<? extends PatternBehavior> patternBehaviorClass() default PatternBehavior.class;

    /**
     * 对内容类型序列化的行为设置
     *
     * @author wuxii@foxmail.com
     */
    public enum Behavior implements PatternBehavior {

        AUTO(""), //
        ARRAY("[*]."), //
        PAGE("items[*]."), //
        NONE("");

        private String prefix;

        Behavior(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Set<String> convert(String... patterns) {
            Set<String> result = new LinkedHashSet<>();
            for (String s : patterns) {
                if (s.startsWith("$.")) {
                    result.add(s.substring(2));
                } else {
                    result.add(prefix + s);
                }
            }
            return result;
        }

    }

    public interface PatternBehavior {

        Set<String> convert(String... patterns);

    }

}

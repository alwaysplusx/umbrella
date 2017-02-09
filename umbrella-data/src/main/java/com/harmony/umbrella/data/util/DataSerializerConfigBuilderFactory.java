package com.harmony.umbrella.data.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;

import com.harmony.umbrella.data.Persistable;
import com.harmony.umbrella.data.domain.BaseEntity;
import com.harmony.umbrella.json.PropertyTransformer;
import com.harmony.umbrella.json.builder.SerializerConfigBuilderFactory;
import com.harmony.umbrella.json.builder.SerializerConfigBuilderFactory.SerializerConfigBuilder;
import com.harmony.umbrella.json.serializer.FilterMode;
import com.harmony.umbrella.json.tsf.AutoPrefixPropertyTransformer;

public class DataSerializerConfigBuilderFactory {

    private static final List<PropertyTransformer> transformers = new ArrayList<>();

    private static final String[] ENTITY_FIELD_NAMES;

    static {

        Field[] fields = BaseEntity.class.getDeclaredFields();
        ENTITY_FIELD_NAMES = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            ENTITY_FIELD_NAMES[i] = fields[i].getName();
        }

        AutoPrefixPropertyTransformer transformer = new AutoPrefixPropertyTransformer();
        transformer.map(Iterable.class, "[*]");
        transformer.map(Collection.class, "[*]");
        transformer.map(Object[].class, "[*]");
        transformer.map(Page.class, "content[*]");
        transformers.add(transformer);
    }

    public static DataSerializerConfigBuilderFactory getDefaultBuilderFactory() {
        return new DataSerializerConfigBuilderFactory(new SerializerConfigBuilderFactory(transformers.toArray(new PropertyTransformer[transformers.size()])));
    }

    private final SerializerConfigBuilderFactory builderFactory;

    public DataSerializerConfigBuilderFactory(SerializerConfigBuilderFactory builderFactory) {
        this.builderFactory = builderFactory;
    }

    public DataSerializerConfigBuilder configFor(Class<?> clazz) {
        return new DataSerializerConfigBuilder(builderFactory, clazz);
    }

    public DataSerializerConfigBuilder configFor(Class<?> clazz, FilterMode defaultFilterMode) {
        return new DataSerializerConfigBuilder(builderFactory, clazz, defaultFilterMode);
    }

    public SerializerConfigBuilderFactory getSerializerConfigBuilderFactory() {
        return builderFactory;
    }

    public class DataSerializerConfigBuilder extends SerializerConfigBuilder<DataSerializerConfigBuilder> {

        private DataSerializerConfigBuilder(SerializerConfigBuilderFactory serializerConfigBuilderFactory, Class<?> type) {
            serializerConfigBuilderFactory.super(type);
        }

        private DataSerializerConfigBuilder(SerializerConfigBuilderFactory serializerConfigBuilderFactory, Class<?> type, FilterMode mode) {
            serializerConfigBuilderFactory.super(type, mode);
        }

        /**
         * 使用特定的过滤模式过滤{@linkplain Persistable}的所有属性
         * 
         * @param mode
         *            过滤模式
         * @return this
         */
        public DataSerializerConfigBuilder withAllPersistableAttribute(FilterMode mode) {
            return withPersistableAttribute(mode, "id", "new");
        }

        /**
         * 使用特地的过滤模式过滤{@linkplain Persistable}的特地属性
         * 
         * @param mode
         *            过滤模式
         * @param attrs
         *            需要过滤的属性
         * @return this
         */
        public DataSerializerConfigBuilder withPersistableAttribute(FilterMode mode, String... attrs) {
            return withAttribute(Persistable.class, attrs);
        }

        /**
         * 使用特定的过滤模式来过滤{@linkplain BaseEntity}的所有属性
         * 
         * @param mode
         *            过滤模式
         * @return this
         */
        public DataSerializerConfigBuilder withAllEntityAttribute(FilterMode mode) {
            return withEntityAttribute(mode, ENTITY_FIELD_NAMES);
        }

        /**
         * 使用特地的过滤模式来过滤特定的{@linkplain BaseEntity}属性
         * 
         * @param mode
         *            过滤模式
         * @param attrs
         *            需要过滤的属性
         * @return this
         */
        public DataSerializerConfigBuilder withEntityAttribute(FilterMode mode, String... attrs) {
            return withAttribute(BaseEntity.class, mode, attrs);
        }

    }
}
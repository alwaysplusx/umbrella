//package com.harmony.umbrella.data.util;
//
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import javax.persistence.FetchType;
//import javax.persistence.ManyToMany;
//import javax.persistence.ManyToOne;
//import javax.persistence.OneToMany;
//import javax.persistence.OneToOne;
//
//import com.alibaba.fastjson.annotation.JSONField;
//import com.alibaba.fastjson.serializer.JSONSerializer;
//import com.alibaba.fastjson.serializer.PropertyPreFilter;
//import com.alibaba.fastjson.serializer.SerializeFilter;
//import com.alibaba.fastjson.serializer.SerializerFeature;
//import com.harmony.umbrella.data.Persistable;
//import com.harmony.umbrella.data.domain.BaseEntity;
//import com.harmony.umbrella.data.domain.Page;
//import com.harmony.umbrella.json.Json;
//import com.harmony.umbrella.json.SerializerConfig;
//import com.harmony.umbrella.util.ReflectionUtils;
//
///**
// * 对数据的序列化工具
// * 
// * @author wuxii@foxmail.com
// */
//public class DataUtils {
//
//    /**
//     * 需要被懒加载处理的注解
//     */
//    private static final List<Class<? extends Annotation>> ANNOTATION_CLASSES = new ArrayList<Class<? extends Annotation>>();
//    static {
//        ANNOTATION_CLASSES.add(ManyToOne.class);
//        ANNOTATION_CLASSES.add(ManyToMany.class);
//        ANNOTATION_CLASSES.add(OneToMany.class);
//        ANNOTATION_CLASSES.add(OneToOne.class);
//    }
//
//    private static final FetchType DEFAULT_FETCH_TYPE = FetchType.LAZY;
//
//    private static final String[] EMPTY_EXCLUDE_FIELD = new String[0];
//
//    private static final SerializerFeature[] EMPTY_SERIALIZER_FEATURE = new SerializerFeature[0];
//
//    /**
//     * 将对象输出为json文本
//     * 
//     * @param object
//     *            待序列化对象
//     * @return json文本
//     * @see #toJson(Object, SerializerFeature[], String...)
//     */
//    public static String toJson(Object object) {
//        return toJson(object, DEFAULT_FETCH_TYPE, EMPTY_SERIALIZER_FEATURE, EMPTY_EXCLUDE_FIELD);
//    }
//
//    /**
//     * 对象输出json文本, 并排除指定的字段
//     * 
//     * @param object
//     *            序列化对象
//     * @param excludes
//     *            排除的字段
//     * @return json文本
//     * @see #toJson(Object, SerializerFeature[], String...)
//     */
//    public static String toJson(Object object, String... excludes) {
//        return toJson(object, DEFAULT_FETCH_TYPE, EMPTY_SERIALIZER_FEATURE, excludes);
//    }
//
//    /**
//     * 带有序列化特性的将对象转为json文本
//     * 
//     * @param object
//     *            序列化对象
//     * @param features
//     *            序列化特性
//     * @return 序列化json文本
//     * @see #toJson(Object, SerializerFeature[], String...)
//     */
//    public static String toJson(Object object, SerializerFeature... features) {
//        return toJson(object, DEFAULT_FETCH_TYPE, features, EMPTY_EXCLUDE_FIELD);
//    }
//
//    /**
//     * 对象输出json文本, 并带有序列化特性以及可以排除指定的字段
//     * <p>
//     * <b>Page对于excludes的特殊情况判断</b>
//     * <ul>
//     * <li>Page对象中默认排除的是{@linkplain Page#getResult()}下数组元素的对应字段
//     * <li>如果打算排除page对象中的指定元素可以采用$.name的方式来排除, 其中$表示page对象本身
//     * </ul>
//     * <p>
//     * <b>集合对excludes的特殊判断</b>
//     * <ul>
//     * <li>排除的默认是数组下元素的属性
//     * <li>如果想排除指定的数组元素可以采用$[index]的方式来来处指定数组index的元素
//     * </ul>
//     * 
//     * @param object
//     *            待序列化对象
//     * @param features
//     *            序列化特性
//     * @param excludes
//     *            排除的字段
//     * @return json文本
//     */
//    public static String toJson(final Object object, SerializerFeature[] features, String... excludes) {
//        return toJson(object, DEFAULT_FETCH_TYPE, features, excludes);
//    }
//
//    /**
//     * 带有fetchType的序列化
//     * <p>
//     * 设置fetchType={@linkplain FetchType#EAGER}能尝试自动抓取entity设置为{@linkplain FetchType#LAZY}的属性值,
//     * 如果设置为{@linkplain FetchType#LAZY}则不抓取entity设置为{@linkplain FetchType#LAZY}的属性
//     * 
//     * @param object
//     *            需要序列化的对象
//     * @param fetchType
//     *            fetch type
//     * @return json文本
//     */
//    public static String toJson(Object object, FetchType fetchType) {
//        return toJson(object, fetchType, EMPTY_SERIALIZER_FEATURE, EMPTY_EXCLUDE_FIELD);
//    }
//
//    /**
//     * 序列化对象为json文本
//     * <p>
//     * 设置fetchType={@linkplain FetchType#EAGER}能尝试自动抓取entity设置为{@linkplain FetchType#LAZY}的属性值,
//     * 如果设置为{@linkplain FetchType#LAZY}则不抓取entity设置为{@linkplain FetchType#LAZY}的属性
//     * 
//     * @param object
//     *            待序列化的对象
//     * @param fetchType
//     *            fetch type
//     * @param excludes
//     *            需要排除的字段
//     * @return json文本
//     */
//    public static String toJson(Object object, FetchType fetchType, String... excludes) {
//        return toJson(object, fetchType, EMPTY_SERIALIZER_FEATURE, EMPTY_EXCLUDE_FIELD);
//    }
//
//    /**
//     * 序列化对象为json文本
//     * <p>
//     * 设置fetchType={@linkplain FetchType#EAGER}能尝试自动抓取entity设置为{@linkplain FetchType#LAZY}的属性值,
//     * 如果设置为{@linkplain FetchType#LAZY}则不抓取entity设置为{@linkplain FetchType#LAZY}的属性
//     * 
//     * @param object
//     *            待序列化的对象
//     * @param fetchType
//     *            fetch type
//     * @param features
//     *            序列化特性
//     * @return json文本
//     */
//    public static String toJson(Object object, FetchType fetchType, SerializerFeature... features) {
//        return toJson(object, fetchType, features, EMPTY_EXCLUDE_FIELD);
//    }
//
//    /**
//     * 序列化对象为json文本
//     * <p>
//     * 设置fetchType={@linkplain FetchType#EAGER}能尝试自动抓取entity设置为{@linkplain FetchType#LAZY}的属性值,
//     * 如果设置为{@linkplain FetchType#LAZY}则不抓取entity设置为{@linkplain FetchType#LAZY}的属性
//     * 
//     * @param object
//     *            待序列化的对象
//     * @param fetchType
//     *            fetch type
//     * @param features
//     *            序列化特性
//     * @param excludes
//     *            需要排除的字段
//     * @return json文本
//     */
//    public static String toJson(final Object object, FetchType fetchType, SerializerFeature[] features, String... excludes) {
//
//        if (object == null) {
//            return "";
//        }
//
//        // 如果是文本类型直接返回
//        if (object instanceof CharSequence) {
//            return object.toString();
//        }
//
//        final Class<?> originClass = object.getClass();
//        final Object wrappedObject;
//
//        // wrapped page class
//        if (Page.class.isAssignableFrom(originClass)) {
//            wrappedObject = new PageWrapper((Page) object);
//        } else {
//            wrappedObject = object;
//        }
//
//        final Class<?> wrappedClass = wrappedObject.getClass();
//
//        DataSerializerConfig cfg = new DataSerializerConfig()//
//                .withEntityFilter()//
//                .withLazyFilter()//
//                .withFetchLazyProperty(FetchType.EAGER.equals(fetchType))//
//                .withTypedFilterProperty(wrappedClass, excludes)//
//                .withSerializerFeature(features)//
//                .withSerializerFeature(SerializerFeature.WriteMapNullValue);
//
//        return toJson(wrappedObject, cfg);
//    }
//
//    public static String toJson(Object object, DataSerializerConfig cfg) {
//        return Json.toJson(object, cfg);
//    }
//
//    /**
//     * 根据类类型创建默认的排除字段
//     * 
//     * @param clazz
//     *            类类型
//     * @param excludes
//     *            外部添加的排除字段
//     * @return 格式化时候需要排除的字段
//     */
//    private static Set<String> createTypedProperty(Class<?> clazz, String... excludes) {
//        Set<String> result = new HashSet<String>();
//        String prefix;
//        if (Page.class.isAssignableFrom(clazz)) {
//            // records[*].x
//            // page 对象排除其数据内容中的字段
//            prefix = "result[*].";
//        } else if (PageWrapper.class.isAssignableFrom(clazz)) {
//            // 同page对象
//            prefix = "records[*].";
//        } else if (clazz.isArray() || Collection.class.isAssignableFrom(clazz)) {
//            // 集合类型中排除指定内容的字段
//            // [*].x
//            prefix = "[*].";
//        } else {
//            prefix = "";
//        }
//
//        for (String property : excludes) {
//            if (property.startsWith("$.")) {
//                result.add(property.substring(2));
//            } else {
//                result.add(prefix + (property.startsWith("*.") ? property.substring(2) : property));
//            }
//        }
//        return result;
//    }
//
//    public static final class PageWrapper<T> {
//
//        private Page<T> page;
//
//        public PageWrapper(Page<T> page) {
//            this.page = page;
//        }
//
//        @JSONField(ordinal = 0)
//        public long getTotalCount() {
//            return page.getTotalElements();
//        }
//
//        @JSONField(ordinal = 1)
//        public List<T> getContent() {
//            return page.getContent();
//        }
//    }
//
//    /**
//     * data/entity序列化配置
//     * 
//     * @author wuxii@foxmail.com
//     */
//    public static final class DataSerializerConfig extends SerializerConfig<DataSerializerConfig> {
//
//        private boolean tryFetch;
//        private boolean entityFilter;
//        private boolean lazyFilter;
//
//        /**
//         * 设置是否过滤BaseEntity下的属性
//         * 
//         * @param flag
//         *            是否过滤标志位
//         * @return DataSerializerConfig
//         * @see EntityPropertyFilter
//         */
//        public DataSerializerConfig withEntityFilter(boolean flag) {
//            this.entityFilter = flag;
//            return this;
//        }
//
//        /**
//         * 自动过滤lazy的entity属性
//         * 
//         * @return DataSerializerConfig
//         * @see LazyPropertyFilter
//         */
//        public DataSerializerConfig withLazyFilter() {
//            return withLazyFilter(true);
//        }
//
//        /**
//         * 设置是否过滤lazy的entity属性
//         * 
//         * @param flag
//         *            是否过滤标志位
//         * @return DataSerializerConfig
//         * @see LazyPropertyFilter
//         */
//        public DataSerializerConfig withLazyFilter(boolean flag) {
//            this.lazyFilter = flag;
//            return this;
//        }
//
//        /**
//         * 设置是否主动尝试加载lazy的entity属性, 如果尝试未加载到或失败则该字段被过滤
//         * 
//         * @param tryFetch
//         *            是否主动尝试的标志位
//         * @return DataSerializerConfig
//         */
//        public DataSerializerConfig withFetchLazyProperty(boolean tryFetch) {
//            this.tryFetch = tryFetch;
//            return this;
//        }
//
//        /**
//         * 按值的类型设置需要忽略的属性值
//         * <p>
//         * 如:
//         * <ul>
//         * <li>page下需要过滤的为: record[*].propertyName
//         * <li>list下需要过滤的为: [*].propertyName
//         * <li>单个实体需过滤的为: propertyName
//         * </ul>
//         * 
//         * @param type
//         *            实体类型
//         * @param property
//         *            需要过滤的属性
//         * @return DataSerializerConfig
//         */
//        public DataSerializerConfig withTypedFilterProperty(Class<?> type, String... property) {
//            if (property.length > 0) {
//                Set<String> properties = createTypedProperty(type, property);
//                withFilterProperty(properties.toArray(new String[0]));
//            }
//            return this;
//        }
//
//        @Override
//        protected SerializeFilter[] buildFilters() {
//            List<SerializeFilter> result = new ArrayList<SerializeFilter>();
//
//            return result.toArray(new SerializeFilter[0]);
//        }
//
//    }
//
//}

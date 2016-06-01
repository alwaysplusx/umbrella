package com.harmony.umbrella.data.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.data.domain.Page;
import com.harmony.umbrella.json.Json;

/**
 * @author wuxii@foxmail.com
 */
public class DataUtils {

    // 默认排除的属性名称
    private static final Set<String> DEFAULT_EXCLUDES = new HashSet<String>(Arrays.asList("*.new", "*.id"));

    private static final SerializerFeature[] EMPTY_FEATURE = new SerializerFeature[0];

    private static final String[] EMPTY_EXCLUDE_FIELD = new String[0];

    /**
     * 将对象输出为json文本
     * 
     * @param object
     *            待序列化对象
     * @return json文本
     * @see #toJson(Object, SerializerFeature[], String...)
     */
    public static String toJson(Object object) {
        return toJson(object, EMPTY_FEATURE, EMPTY_EXCLUDE_FIELD);
    }

    /**
     * 带有序列化特性的将对象转为json文本
     * 
     * @param object
     *            序列化对象
     * @param features
     *            序列化特性
     * @return 序列化json文本
     * @see #toJson(Object, SerializerFeature[], String...)
     */
    public static String toJson(Object object, SerializerFeature... features) {
        return toJson(object, features, EMPTY_EXCLUDE_FIELD);
    }

    /**
     * 对象输出json文本, 并排除指定的字段
     * 
     * @param object
     *            序列化对象
     * @param excludes
     *            排除的字段
     * @return json文本
     * @see #toJson(Object, SerializerFeature[], String...)
     */
    public static String toJson(Object object, String... excludes) {
        return toJson(object, EMPTY_FEATURE, excludes);
    }

    /**
     * 对象输出json文本, 并带有序列化特性以及可以排除指定的字段
     * <p>
     * <b>Page对于excludes的特殊情况判断</b>
     * <ul>
     * <li>Page对象中默认排除的是{@linkplain Page#getResult()}下数组元素的对应字段
     * <li>如果打算排除page对象中的指定元素可以采用$.name的方式来排除, 其中$表示page对象本身
     * </ul>
     * <p>
     * <b>集合对excludes的特殊判断</b>
     * <ul>
     * <li>排除的默认是数组下元素的属性
     * <li>如果想排除指定的数组元素可以采用$[index]的方式来来处指定数组index的元素
     * </ul>
     * 
     * @param object
     *            待序列化对象
     * @param features
     *            序列化特性
     * @param excludes
     *            排除的字段
     * @return json文本
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String toJson(Object object, SerializerFeature[] features, String... excludes) {
        if (object == null) {
            return "";
        }
        if (object instanceof String) {
            return (String) object;
        }
        Set<String> excludeSet;
        Class<?> clazz = object.getClass();
        if (Page.class.isAssignableFrom(clazz)) {
            object = new PageWrapper((Page) object);
            excludeSet = createExcludeProperty(PageWrapper.class, excludes);
        } else {
            excludeSet = createExcludeProperty(clazz, excludes);
        }
        return Json.toJson(object, excludeSet.toArray(new String[excludeSet.size()]), features);
    }

    private static Set<String> createExcludeProperty(Class<?> clazz, String... excludes) {
        Set<String> result = new HashSet<String>(DEFAULT_EXCLUDES);
        String prefix;
        if (Page.class.isAssignableFrom(clazz)) {
            // records[*].x
            prefix = "result[*].";
        } else if (PageWrapper.class.isAssignableFrom(clazz)) {
            prefix = "records[*].";
        } else if (clazz.isArray() || Collection.class.isAssignableFrom(clazz)) {
            // [*].x
            prefix = "[*].";
        } else {
            prefix = "";
        }
        for (String property : excludes) {
            if (property.startsWith("$.")) {
                result.add(property.substring(2));
            } else {
                result.add(prefix + (property.startsWith("*.") ? property.substring(2) : property));
            }
        }
        return result;
    }

    public static final class PageWrapper<T> {

        private Page<T> page;

        public PageWrapper(Page<T> page) {
            this.page = page;
        }

        @JSONField(ordinal = 0)
        public long getTotalCount() {
            return page.getTotalElements();
        }

        @JSONField(ordinal = 1)
        public List<T> getRecords() {
            return page.getContent();
        }
    }
}

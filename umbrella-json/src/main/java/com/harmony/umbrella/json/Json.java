package com.harmony.umbrella.json;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.json.serializer.SimplePropertyNameFilter;

/**
 * @author wuxii@foxmail.com
 */
public class Json {

    /**
     * 将Object对象序列化为json文本
     * 
     * @param object
     *            待序列化的对象
     * @return json文本
     */
    public static String toJson(Object object) {
        return toJson(object, new SerializeFilter[0]);
    }

    /**
     * 将Object对象序列化为json文本
     * <p>
     * 提供基础特性
     * <ul>
     * <li>UseSingleQuotes:单引号
     * <li>PrettyFormat:格式化输出
     * <li>...
     * </ul>
     * 
     * @param object
     *            待序列化对象
     * @param features
     *            序列化特性
     * @return json文本
     * @see SerializerFeature
     */
    public static String toJson(Object object, SerializerFeature... features) {
        return JSON.toJSONString(object, features);
    }

    /**
     * 将Object序列化输出为json文本
     * 
     * @param object
     *            待序列化对象
     * @param filters
     *            序列化过滤工具
     * @return json文本
     * 
     * @see com.alibaba.fastjson.serializer.SerializeFilter
     * @see com.alibaba.fastjson.serializer.BeforeFilter
     * @see com.alibaba.fastjson.serializer.AfterFilter
     * @see com.alibaba.fastjson.serializer.PropertyPreFilter
     * @see com.alibaba.fastjson.serializer.PropertyFilter
     * @see com.alibaba.fastjson.serializer.NameFilter
     * @see com.alibaba.fastjson.serializer.ValueFilter
     */
    public static String toJson(Object object, SerializeFilter... filters) {
        return JSON.toJSONString(object, filters);
    }

    /**
     * 将Object序列化输出为json文本
     * 
     * @param object
     *            待序列化对象
     * @param features
     *            序列化特性
     * @param filters
     *            序列化过滤工具
     * @return json文本
     * 
     * @see #toJson(Object, SerializeFilter...)
     * @see #toJson(Object, SerializerFeature...)
     */
    public static String toJson(Object object, SerializeFilter[] filters, SerializerFeature... features) {
        return JSON.toJSONString(object, filters, features);
    }

    /**
     * 将Object序列化为Json文本， 并对列出的{@code excludes}不在json中显示出来
     * 
     * @param object
     *            待序列化对象
     * @param excludeProperties
     *            object中被忽略的字段名称
     * @return json文本
     */
    public static String toJson(final Object object, final String... excludeProperties) {
        return toJson(object, excludeProperties, new SerializerFeature[0]);
    }

    public static String toJson(final Object object, final String[] excludeProperties, SerializerFeature... features) {
        return JSON.toJSONString(object, new SimplePropertyNameFilter(excludeProperties), features);
    }

    public static Map<String, Object> toMap(String json) {
        return JSON.parseObject(json);
    }

    public static List<?> toArray(String json) {
        return JSON.parseArray(json);
    }
    
    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }

    public static <T> T parse(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

}

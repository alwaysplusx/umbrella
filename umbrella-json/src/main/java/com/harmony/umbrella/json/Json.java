package com.harmony.umbrella.json;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.json.serializer.SimplePatternFilter;

/**
 * json序列化工具
 * 
 * @author wuxii@foxmail.com
 */
public class Json {

    private static final SerializerFeature[] EMPTY_FEATURE = new SerializerFeature[0];

    private static final SerializeFilter[] EMPTY_FILTER = new SerializeFilter[0];

    // serialize

    /**
     * 将Object对象序列化为json文本
     * 
     * @param object
     *            待序列化的对象
     * @return json文本
     */
    public static String toJson(Object object) {
        return toJson(object, EMPTY_FILTER, EMPTY_FEATURE);
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
        return toJson(object, EMPTY_FILTER, features);
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
        return toJson(object, filters, EMPTY_FEATURE);
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
    public static String toJson(Object object, String... excludeProperties) {
        return toJson(object, excludeProperties, EMPTY_FEATURE);
    }

    /**
     * 格式化输出json文本， 并带有排除字段选项，以及格式换特性
     * 
     * @param object
     *            待序列化对象
     * @param excludeProperties
     *            格式化所排除的字段
     * @param features
     *            序列化特性
     * @return 格式化后的json文本
     */
    public static String toJson(Object object, String[] excludeProperties, SerializerFeature... features) {
        return toJson(object, new SerializeFilter[] { new SimplePatternFilter(excludeProperties) }, features);
    }

    public static String toJson(Object object, SerializerConfig<?> cfg) {
        SerializeFilter[] filters = cfg.getFilters();
        SerializerFeature[] features = cfg.getFeatures();
        SerializeConfig scfg = cfg.getSerializeConfig();
        if (scfg == null) {
            return JSON.toJSONString(object, filters, features);
        }
        return JSON.toJSONString(object, scfg, filters, features);
    }

    // deserialize

    /**
     * 将json文本转为map对象
     * 
     * @param json
     *            json文本
     * @return map
     */
    public static Map<String, Object> parseMap(String json) {
        return parseMap(json, new Feature[0]);
    }

    /**
     * 将json文本转为map对象
     * 
     * @param json
     *            json文本
     * @return map
     */
    public static Map<String, Object> parseMap(String json, Feature... features) {
        return JSON.parseObject(json, features);
    }

    /**
     * 将array格式的json文本转为list对象
     * 
     * @param json
     *            json文本
     * @return list
     */
    public static List<Object> parseArray(String json) {
        return parseArray(json, Object.class);
    }

    /**
     * @see Json#parseArray(String)
     */
    public static <T> List<T> parseArray(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }

    /**
     * 将json文本转为对象
     * 
     * @param json
     *            json文本
     * @return object
     */
    public static Object parse(String json) {
        return JSON.parseObject(json, Object.class);
    }

    /**
     * 将json文本转为java对象
     * 
     * @param json
     *            json文本
     * @param clazz
     *            需要转化的目标类
     * @return target obejct
     */
    public static <T> T parse(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

}

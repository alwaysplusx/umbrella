/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @author wuxii@foxmail.com
 */
public abstract class Json {

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

    public static String toJson(Object object, SerializeFilter filter, SerializerFeature... features) {
        return toJson(object, new SerializeFilter[] { filter }, features);
    }

    /**
     * 将Object序列化为Json文本， 并对列出的{@code excludes}不在json中显示出来
     * 
     * @param object
     *            待序列化对象
     * @param excludes
     *            object中被忽略的字段名称
     * @return json文本
     */
    public static String toJson(final Object object, final String... excludes) {
        return toJson(object, excludes, new SerializerFeature[0]);
    }

    public static String toJson(final Object object, final String[] excludes, SerializerFeature... features) {
        return null;
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

}

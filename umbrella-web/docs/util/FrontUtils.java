/*
 * Copyright 2002-2015 the original author or authors.
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
package com.harmony.umbrella.web.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.data.domain.Page;
import com.harmony.umbrella.json.Json;
import com.harmony.umbrella.json.serializer.SimplePropertyNameFilter;

/**
 * @author wuxii@foxmail.com
 */
public abstract class FrontUtils {

    private static final Set<String> DEFAULT_EXCLUDES = new HashSet<String>(Arrays.asList("*.id", "*.new"));

    public static String getRequestUrl(HttpServletRequest request) {
        return request.getRequestURI().toString();
    }

    /**
     * 将page包装为前台需要的样子
     * 
     * @param page
     *            page对象
     * @return {@linkplain FrontPage}
     */
    public static FrontPage wrapPage(Page<?> page) {
        return new FrontPage(page);
    }

    /**
     * 将page用json输出， 输出如{@linkplain FrontPage}
     * 
     * @param page
     *            page对象
     * @return page的json文本
     */
    public static String toJson(Page<?> page) {
        return Json.toJson(wrapPage(page));
    }

    /**
     * 将page用json输出， 输出如{@linkplain FrontPage}
     * <p>
     * 并排除指定的字段{@code excludes}
     * <p>
     * <b>excludes 排除的为 {@linkplain FrontPage#getRecords()}下的字段
     * <p>
     * 如果要排除 {@linkplain FrontPage}下的对象加[{@code $.[fieldName]}], $.表示根节点</b>
     * 
     * @param page
     *            page对象
     * @return page的json文本
     */
    public static String toJson(Page<?> page, String... excludes) {
        return Json.toJson(wrapPage(page), new SimplePropertyNameFilter(createExcludeProperty(Page.class, excludes), true));
    }

    /**
     * 定制特性输出page json文本
     * 
     * @param page
     *            page对象
     * @param features
     *            序列化特性
     * @return json文本
     */
    public static String toJson(Page<?> page, SerializerFeature... features) {
        return Json.toJson(wrapPage(page), features);
    }

    /**
     * 将page用json输出， 输出如{@linkplain FrontPage}
     * <p>
     * 并排除指定的字段{@code excludes}
     * <p>
     * <b>excludes 排除的为 {@linkplain FrontPage#getRecords()}下的字段
     * <p>
     * 如果要排除 {@linkplain FrontPage}下的对象加[{@code $.[fieldName]}], $.表示根节点</b>
     * 
     * @param page
     *            page对象
     * @param features
     *            序列化特性
     * @param excludes
     *            排除的字段
     * @return json文本
     * @see #toJson(Page)
     * @see #toJson(Page, SerializerFeature...)
     */
    public static String toJson(Page<?> page, SerializerFeature[] features, String... excludes) {
        return Json.toJson(wrapPage(page), new SerializeFilter[] { new SimplePropertyNameFilter(createExcludeProperty(Page.class, excludes), true) }, features);
    }

    /**
     * 格式化输出数组对象
     * 
     * @param content
     *            集合对象
     * @return json文本
     */
    public static String toJson(Collection<?> content) {
        return Json.toJson(content);
    }

    /**
     * 格式化输出数组对象
     * <p>
     * <b> excludes 排除的为所有数组元素下的对象， 如果要直接排除某个节点的数组加$.</b>
     * 
     * @param content
     *            集合对象
     * @param excludes
     *            排除的字段
     * @return json文本
     */
    public static String toJson(Collection<?> content, String... excludes) {
        return Json.toJson(content, new SimplePropertyNameFilter(createExcludeProperty(Collection.class, excludes), true));
    }

    /**
     * 集合类转为json文本
     * 
     * @param content
     *            集合对象
     * @param features
     *            序列化特性
     * @return json文本
     */
    public static String toJson(Collection<?> content, SerializerFeature... features) {
        return Json.toJson(content, features);
    }

    /**
     * 格式化输出数组对象
     * <p>
     * <b> excludes 排除的为所有数组元素下的对象， 如果要直接排除某个节点的数组加$.</b>
     * 
     * @param content
     *            集合对象
     * @param features
     *            序列化特性
     * @param excludes
     *            排除的字段
     * @return json文本
     */
    public static String toJson(Collection<?> content, SerializerFeature[] features, String... excludes) {
        return Json.toJson(content, new SerializeFilter[] { new SimplePropertyNameFilter(createExcludeProperty(Collection.class, excludes), true) }, features);
    }

    /**
     * 将对象输出为json文本
     * 
     * @param object
     *            待序列化对象
     * @return json文本
     */
    public static String toJson(Object object) {
        return Json.toJson(object);
    }

    /**
     * 对象输出json文本
     * 
     * @param object
     *            序列化对象
     * @param excludes
     *            排除的字段
     * @return json文本
     */
    public static String toJson(Object object, String... excludes) {
        return Json.toJson(object, new SimplePropertyNameFilter(createExcludeProperty(object.getClass(), excludes), true));
    }

    /**
     * 对象输出jsn文本
     * 
     * @param object
     *            待序列化对象
     * @param features
     *            序列化特性
     * @param excludes
     *            排除的字段
     * @return json文本
     */
    public static String toJson(Object object, SerializerFeature[] features, String... excludes) {
        return Json.toJson(object, new SerializeFilter[] { new SimplePropertyNameFilter(createExcludeProperty(object.getClass(), excludes), true) }, features);
    }

    private static Set<String> createExcludeProperty(Class<?> clazz, String... excludes) {
        Set<String> result = new HashSet<String>(DEFAULT_EXCLUDES);
        String prefix;
        if (Page.class.isAssignableFrom(clazz)) {
            // records[*].x
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
}

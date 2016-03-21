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
package com.harmony.umbrella.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.harmony.umbrella.io.resource.ClassPathResource;

/**
 * 属性加载工具
 *
 * @author wuxii@foxmail.com
 */
public abstract class PropUtils {

    /**
     * 判断是否存在且是资源文件
     *
     * @param path
     *            文件路径
     */
    public static boolean exists(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            return true;
        }
        InputStream inStream = null;
        try {
            inStream = new ClassPathResource(path).getInputStream();
        } catch (IOException e) {
            return false;
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                }
            }
        }
        return inStream != null;
    }

    /**
     * 如果所对应的资源文件不存在忽略
     */
    public static Properties loadPropertiesSilently(String... paths) {
        Properties props = new Properties();
        if (paths == null || paths.length == 0) {
            return props;
        }
        for (String path : paths) {
            try {
                loadProperties(path, props);
            } catch (IOException e) {
                // ignore
            }
        }
        return props;
    }

    /**
     * 加载指定文件下的资源文件
     *
     * @param paths
     *            资源文件的路径
     * @return 资源文件中的属性
     * @throws IOException
     */
    public static Properties loadProperties(String... paths) throws IOException {
        Properties props = new Properties();
        if (paths == null || paths.length == 0) {
            return props;
        }
        for (String path : paths) {
            loadProperties(path, props);
        }
        return props;
    }

    private static void loadProperties(String path, Properties props) throws IOException {
        InputStream inStream = null;
        try {
            inStream = new ClassPathResource(path).getInputStream();
            props.load(inStream);
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 系统{@linkplain java.lang.System#getProperty(String)}
     *
     * @param key
     *            环境属性key
     * @return 系统环境属性值
     */
    public static String getSystemProperty(String key) {
        return System.getProperty(key);
    }

    /**
     * 系统{@linkplain System#getProperty(String)}
     *
     * @param key
     *            环境属性key
     * @param defaultValue
     *            默认值
     * @return 系统环境属性值
     */
    public static String getSystemProperty(String key, String defaultValue) {
        return System.getProperty(key, defaultValue);
    }

    /**
     * 从属性中过滤出前缀为指定值的属性集合
     *
     * @param prefix
     *            前缀
     * @param props
     *            待过滤的属性
     * @return 前缀符合要求的新属性集合
     */
    public static Properties filterStartWith(String prefix, Properties props) {
        return filterStartWith(prefix, props, false);
    }

    /**
     * 在属性集合中过滤出前缀相同的属性集合
     *
     * @param prefix
     *            key的前缀
     * @param props
     *            属性集合
     * @param ignoreCase
     *            是否忽略大小写
     * @return key前缀相同的属性集合
     */
    public static Properties filterStartWith(String prefix, Properties props, boolean ignoreCase) {
        Assert.notBlank(prefix, "prefix not allow null or blank");
        Properties result = new Properties();
        if (props == null || props.isEmpty()) {
            return result;
        }
        Set<String> propertyNames = props.stringPropertyNames();
        for (String name : propertyNames) {
            if (name.startsWith(prefix) || (ignoreCase && name.toLowerCase().startsWith(prefix.toLowerCase()))) {
                result.put(name, props.get(name));
            }
        }
        return result;
    }

    /**
     * map中过滤出key前缀相同的属性集合
     *
     * @param prefix
     *            key前缀
     * @param map
     *            属性集合
     * @return key前缀相同的属性集合
     */
    public static Map<String, Object> filterStartWith(String prefix, Map<String, Object> map) {
        return filterStartWith(prefix, map, false);
    }

    /**
     * map中过滤出key前缀相同的属性集合
     *
     * @param prefix
     *            key前缀
     * @param map
     *            属性集合
     * @param ignoreCase
     *            是否忽略大小写
     * @return key前缀相同的属性集合
     */
    public static Map<String, Object> filterStartWith(String prefix, Map<String, Object> map, boolean ignoreCase) {
        Assert.notBlank(prefix, "prefix not allow null or blank");
        Map<String, Object> result = new HashMap<String, Object>();
        Set<String> keys = map.keySet();
        for (String key : keys) {
            if (key.startsWith(prefix) || (ignoreCase && key.toLowerCase().startsWith(prefix.toLowerCase()))) {
                result.put(key, map.get(key));
            }
        }
        return result;
    }

}

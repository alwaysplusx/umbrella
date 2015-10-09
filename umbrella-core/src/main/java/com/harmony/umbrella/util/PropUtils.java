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
import java.util.Iterator;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.io.ClassPathResource;

/**
 * 属性加载工具
 * 
 * @author wuxii@foxmail.com
 */
public abstract class PropUtils {

    private static final Logger log = LoggerFactory.getLogger(PropUtils.class);

    /**
     * 判断是否存在且是资源文件
     * 
     * @param path
     *            文件路径
     */
    public static boolean exists(String path) {
        File file = new File(path);
        return file.exists() && file.isFile();
    }

    /**
     * 加载指定文件下的资源文件
     * 
     * @param filePath
     * @return
     * @throws IOException
     */
    public static Properties loadProperties(String... paths) {
        Properties props = new Properties();
        if (paths == null || paths.length == 0) {
            return props;
        }
        for (String path : paths) {
            loadProperties(path, props);
        }
        return props;
    }

    private static void loadProperties(String path, Properties props) {
        InputStream inStream = null;
        try {
            inStream = new ClassPathResource(path).getInputStream();
            props.load(inStream);
        } catch (IOException e) {
            log.warn("properties file {} not exists", path);
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
     * 系统{@linkplain System#getProperties(String)}
     * 
     * @param key
     * @return
     */
    public static String getSystemProperty(String key) {
        return System.getProperty(key);
    }

    /**
     * 系统{@linkplain System#getProperties(String)}
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getSystemProperty(String key, String defaultValue) {
        return System.getProperty(key, defaultValue);
    }

    public static Properties filterStartWith(String prefix, Properties props) {
        Properties result = new Properties();
        if (props == null || props.isEmpty()) {
            return result;
        }

        Iterator<String> iterator = props.stringPropertyNames().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (key.startsWith(prefix)) {
                result.put(key, props.get(key));
            }
        }
        return result;
    }

}

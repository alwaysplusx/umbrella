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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.harmony.umbrella.io.ClassPathResource;

/**
 * 属性加载工具
 * 
 * @author wuxii@foxmail.com
 */
public abstract class PropUtils {

    /**
     * 加载指定文件下的资源文件
     * 
     * @param filePath
     * @return
     * @throws IOException
     */
    public static Properties loadProperties(String... paths) throws IOException {
        Properties props = new Properties();
        if (paths == null || paths.length == 0)
            return props;
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
                inStream.close();
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
}

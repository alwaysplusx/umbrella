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
package com.harmony.modules.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 属性加载工具
 * @author wuxii@foxmail.com
 */
public class PropUtils {

    /**
     * 加载指定文件下的资源文件
     * @param filePath
     * @return
     * @throws IOException
     */
    public static Properties loadProperties(String filePath) throws IOException {
        Properties props = new Properties();
        InputStream resourceStream = null;
        try {
            resourceStream = ClassUtils.getDefaultClassLoader().getResourceAsStream(filePath);
            if (resourceStream == null) {
                throw new IOException("file " + filePath + " not find ");
            }
            props.load(resourceStream);
        } finally {
            resourceStream.close();
        }
        return props;
    }

    /**
     * 系统{@linkplain System#getProperties(String)}
     * @param key
     * @return
     */
    public static String getSystemProperty(String key) {
        String value = System.getProperty(key);
        return value;
    }

    /**
     * 系统{@linkplain System#getProperties(String)}
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getSystemProperty(String key, String defaultValue) {
        String value = getSystemProperty(key, defaultValue);
        return value;
    }
}

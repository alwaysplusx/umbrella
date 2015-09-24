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
package com.harmony.umbrella;

import java.util.Iterator;
import java.util.Properties;

import com.harmony.umbrella.util.PropUtils;

/**
 * umbrella工程的常量池
 * 
 * @author wuxii@foxmail.com
 */
public abstract class Constants {

    private static final String UMBRELLA_PROPERTIES_LOCATION = "umbrella.properties";

    private static final Properties globalProperties = new Properties();

    public static final String DEFAULT_PACKAGE;

    static {

        globalProperties.putAll(PropUtils.loadProperties(UMBRELLA_PROPERTIES_LOCATION));

        DEFAULT_PACKAGE = globalProperties.getProperty("default.package", "com.harmony");

    }

    public static final Properties getPropertiesStartWith(String prefix) {
        Iterator<String> iterator = globalProperties.stringPropertyNames().iterator();
        Properties props = new Properties();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (key.startsWith(prefix)) {
                props.setProperty(key, globalProperties.getProperty(key));
            }
        }
        return props;
    }

    public static final String getProperty(String key) {
        return globalProperties.getProperty(key);
    }

    public static final String getProperty(String key, String defaultValue) {
        return globalProperties.getProperty(key, defaultValue);
    }

}

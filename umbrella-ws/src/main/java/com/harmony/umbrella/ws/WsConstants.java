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
package com.harmony.umbrella.ws;

import java.util.Properties;

import com.harmony.umbrella.util.PropUtils;

/**
 * @author wuxii@foxmail.com
 */
public class WsConstants {

    public static final String WS_PROPERTIES_FILE_LOCATION = "ws.properties";

    public static final String SERVER_CATEGORY = "WS-100000";

    public static final String PROXY_CATEGORY = "WP-100000";

    public static final String SOAP_CATEGORY = "SP-1000000";

    /**
     * not require, 不需要同步 = -1
     */
    public static final int SYNC_NOT_REQUIRED = -1;

    /**
     * not yet, 未同步 = 1
     */
    public static final int SYNC_NOT_YET = 1;
    /**
     * syncing, 正在同步中 = 2
     */
    public static final int SYNC_SYNCING = 2;
    /**
     * sync completed， 同步完成 = 3
     */
    public static final int SYNC_COMPLETED = 3;
    /**
     * sync exception, 同步异常 = 4
     */
    public static final int SYNC_EXCEPTION = 4;

    private static final Properties WS_PROPERTIES = new Properties();

    static {
        WS_PROPERTIES.putAll(PropUtils.loadProperties(WS_PROPERTIES_FILE_LOCATION));
    }

    public static boolean containsKey(String key) {
        return WS_PROPERTIES.containsKey(key);
    }

    /**
     * 获取ws的常量条件
     */
    public static final String getProperty(String key) {
        return getProperty(key, null);
    }

    /**
     * 获取ws的常量条件
     */
    public static final String getProperty(String key, String defaultValue) {
        return WS_PROPERTIES.getProperty(key, defaultValue);
    }
}

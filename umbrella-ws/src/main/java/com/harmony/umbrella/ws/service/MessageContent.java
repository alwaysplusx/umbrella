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
package com.harmony.umbrella.ws.service;

import java.util.HashMap;

import com.harmony.umbrella.util.StringUtils;

/**
 * 作为服务的返回的服务消息的内容
 * 
 * @author wuxii@foxmail.com
 */
public class MessageContent extends HashMap<String, String> {

    private static final long serialVersionUID = 8111278858572162151L;

    /**
     * 在原有的key基础上添加字符, 如果原来不存在对应的key则直接添加
     * 
     * @param key
     *            key值
     * @param value
     *            对应的值
     */
    public void append(String key, String value) {
        String msg = get(key);
        if (StringUtils.isBlank(msg)) {
            put(key, value);
        } else {
            put(key, String.format("%s, %s", msg, value));
        }
    }
}

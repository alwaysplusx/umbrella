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
package com.harmony.umbrella.context.current;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.harmony.umbrella.context.CurrentContext;

/**
 * @author wuxii@foxmail.com
 */
public class DefaultCurrentContext implements CurrentContext {

    private static final long serialVersionUID = -6615913783364197153L;

    protected Map<String, Object> dataMap = new HashMap<String, Object>();

    public DefaultCurrentContext() {
    }

    public DefaultCurrentContext(Long userId, String username, String remoteHost) {
        this(userId, username, remoteHost, null);
    }

    public DefaultCurrentContext(Long userId, String username, String remoteHost, Map<String, Object> dataMap) {
        this.setUserId(userId);
        this.setUsername(username);
        this.setRemoteHost(remoteHost);
        if (dataMap != null)
            this.dataMap.putAll(dataMap);
    }

    @Override
    public String getUsername() {
        return (String) dataMap.get(USERNAME);
    }

    @Override
    public String getRemoteHost() {
        return (String) dataMap.get(REMOTE_HOST);
    }

    @Override
    public boolean isHttpContext() {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String name) {
        return (T) dataMap.get(name);
    }

    @Override
    public void put(String name, Object o) {
        dataMap.put(name, o);
    }

    @Override
    public Enumeration<String> getCurrentNames() {
        return new Enumeration<String>() {

            Iterator<String> it = dataMap.keySet().iterator();

            @Override
            public String nextElement() {
                return it.next();
            }

            @Override
            public boolean hasMoreElements() {
                return it.hasNext();
            }
        };
    }

    @Override
    public boolean contains(String name) {
        return dataMap.containsKey(name);
    }

    public void setUsername(String username) {
        dataMap.put(USERNAME, username);
    }

    public void setRemoteHost(String remoteHost) {
        dataMap.put(REMOTE_HOST, remoteHost);
    }

    public void putAll(Map<String, Object> data) {
        if (data != null)
            this.dataMap.putAll(data);
    }

    @Override
    public Long getUserId() {
        return (Long) dataMap.get(USER_ID);
    }

    public void setUserId(Long userId) {
        dataMap.put(USER_ID, userId);
    }

}

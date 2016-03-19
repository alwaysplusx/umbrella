/*
 * Copyright 2012-2016 the original author or authors.
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
package com.harmony.umbrella.context;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * @author wuxii@foxmail.com
 */
public class DefaultCurrentContext implements CurrentContext {

    private static final long serialVersionUID = 413466926822406980L;

    protected final Map<String, Object> context = new HashMap<String, Object>();

    protected Locale locale;

    @Override
    public <T> T getUser() {
        return get(USER);
    }

    @Override
    public <T> T getUserId() {
        return get(USER_ID);
    }

    public void setUserId(Object userId) {
        put(USER_ID, userId);
    }

    @Override
    public String getUsername() {
        return get(USER_NAME);
    }

    public void setUsername(String username) {
        put(USER_NAME, username);
    }

    @Override
    public String getNickname() {
        return get(USER_NICKNAME);
    }

    public void setNickName(String nickname) {
        put(USER_NICKNAME, nickname);
    }

    @Override
    public boolean isAuthenticated() {
        return getUserId() != null;
    }

    @Override
    public <T> T getClientId() {
        return get(CLIENT_ID);
    }

    public void setClientId(Object clientId) {
        put(CLIENT_ID, clientId);
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public boolean isHttpContext() {
        return false;
    }

    @Override
    public boolean containsKey(String name) {
        return context.containsKey(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String name) {
        return (T) context.get(name);
    }

    @Override
    public void put(String name, Object o) {
        context.put(name, o);
    }

    @Override
    public Enumeration<String> getCurrentNames() {
        return new Enumeration<String>() {

            private final Iterator<String> iterator = context.keySet().iterator();

            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public String nextElement() {
                return iterator.next();
            }
        };
    }

}

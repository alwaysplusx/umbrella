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
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author wuxii@foxmail.com
 */
public class DefaultHttpCurrentContext implements HttpCurrentContext {

    private static final long serialVersionUID = 1379790597833043807L;

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private HttpSession session;
    protected Locale locale;

    public DefaultHttpCurrentContext(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.session = request.getSession(false);
    }

    @Override
    public <T> T getUser() {
        return getSessionAttribute(USER);
    }

    @Override
    public <T> T getUserId() {
        return getSessionAttribute(USER_ID);
    }

    @Override
    public String getUsername() {
        return getSessionAttribute(USER_NAME);
    }

    @Override
    public String getNickname() {
        return getSessionAttribute(USER_NICKNAME);
    }

    @Override
    public boolean isAuthenticated() {
        return getUserId() != null;
    }

    @Override
    public <T> T getClientId() {
        return getSessionAttribute(CLIENT_ID);
    }

    @Override
    public Locale getLocale() {
        if (locale == null) {
            locale = request.getLocale();
        }
        return locale;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public boolean isHttpContext() {
        return true;
    }

    @Override
    public boolean containsKey(String name) {
        return request.getAttribute(name) != null;
    }

    @Override
    public Enumeration<String> getCurrentNames() {
        return request.getAttributeNames();
    }

    @Override
    public String getCharacterEncoding() {
        return request.getCharacterEncoding();
    }

    @Override
    public HttpServletRequest getHttpRequest() {
        return request;
    }

    @Override
    public HttpServletResponse getHttpResponse() {
        return response;
    }

    @Override
    public boolean sessionCreated() {
        return session != null;
    }

    @Override
    public HttpSession getHttpSession() {
        if (session == null) {
            this.session = request.getSession();
        }
        return session;
    }

    @Override
    public String getSessionId() {
        return getHttpSession().getId();
    }

    @SuppressWarnings("unchecked")
    public <T> T getSessionAttribute(String name) {
        return (T) getHttpSession().getAttribute(name);
    }

    public void setSessionAttribute(String name, Object o) {
        getHttpSession().setAttribute(name, o);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String name) {
        return (T) request.getAttribute(name);
    }

    @Override
    public void put(String name, Object o) {
        request.setAttribute(name, o);
    }

}

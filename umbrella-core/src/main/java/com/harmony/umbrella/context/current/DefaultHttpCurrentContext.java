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
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.harmony.umbrella.context.HttpCurrentContext;
import com.harmony.umbrella.context.MessageBundle;

/**
 * @author wuxii@foxmail.com
 */
public class DefaultHttpCurrentContext implements HttpCurrentContext {

    private static final long serialVersionUID = -7923350971915932542L;

    private static final int ONEDAY = 1000 * 60 * 60 * 24;

    protected HttpServletRequest request;

    protected HttpServletResponse response;

    protected Locale locale;

    private HttpSession session;

    public DefaultHttpCurrentContext() {
    }

    public DefaultHttpCurrentContext(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.session = request.getSession(false);
    }

    @Override
    public Long getUserId() {
        return (Long) getAttribute(USER_ID);
    }

    @Override
    public String getUsername() {
        return (String) getSessionAttribute(USERNAME);
    }

    @Override
    public String getRemoteHost() {
        return request.getRemoteHost();
    }

    @Override
    public boolean isHttpContext() {
        return true;
    }

    @Override
    public String getCharacterEncoding() {
        return request.getCharacterEncoding();
    }

    @Override
    public Locale getLocale() {
        return locale == null ? request.getLocale() : locale;
    }

    @Override
    public void setLocale(Locale locale) {
        this.response.setLocale(locale);
        this.locale = MessageBundle.getLocale(locale);
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
        return getSession();
    }

    @Override
    public String getSessionId() {
        return getHttpSession().getId();
    }

    @Override
    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }

    @Override
    public Object getSessionAttribute(String name) {
        return getHttpSession().getAttribute(name);
    }

    @Override
    public Cookie[] getHttpCookies() {
        return request.getCookies();
    }

    @Override
    public String getParameter(String name) {
        return request.getParameter(name);
    }

    @Override
    public void setAttribute(String name, Object o) {
        request.setAttribute(name, o);
    }

    @Override
    public void setSessionAttribute(String name, Object o) {
        getHttpSession().setAttribute(name, o);
    }

    public String getCookieValue(String name) {
        for (Cookie cookie : getHttpCookies()) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    @Override
    public void addCookie(Cookie cookie) {
        response.addCookie(cookie);
    }

    public void addCookie(String name, String value) {
        addCookie(name, value, ONEDAY);
    }

    @Override
    public void addCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        addCookie(cookie);
    }

    @Override
    public boolean containsParameter(String name) {
        return getParameter(name) != null;
    }

    @Override
    public boolean containsAttribute(String name) {
        return getAttribute(name) != null;
    }

    @Override
    public boolean containsSessionAttribute(String name) {
        return getSessionAttribute(name) != null;
    }

    @Override
    public boolean containsCookie(String name) {
        return getCookieValue(name) != null;
    }

    @Override
    public boolean contains(String name) {
        return containsParameter(name) || containsAttribute(name) || containsSessionAttribute(name) || containsCookie(name);
    }

    @Override
    public <T> T get(String name) {
        return get(name, SCOPE_REQUEST);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String name, int scope) {
        Object result = null;
        switch (scope < SCOPE_REQUEST ? SCOPE_REQUEST : scope > SCOPE_COOKIE ? SCOPE_COOKIE : SCOPE_SESSION) {
        case SCOPE_REQUEST:
            result = getAttribute(name);
            break;
        case SCOPE_SESSION:
            result = getSessionAttribute(name);
            break;
        case SCOPE_COOKIE:
            result = getCookieValue(name);
            break;
        }
        return (T) result;
    }

    @Override
    public void put(String name, Object o) {
        put(name, o, SCOPE_REQUEST);
    }

    @Override
    public void put(String name, Object o, int scope) {
        switch (scope < SCOPE_REQUEST ? SCOPE_REQUEST : scope > SCOPE_COOKIE ? SCOPE_COOKIE : SCOPE_SESSION) {
        case SCOPE_REQUEST:
            setAttribute(name, o);
            break;
        case SCOPE_SESSION:
            setSessionAttribute(name, o);
            break;
        case SCOPE_COOKIE:
            addCookie(name, o == null ? null : o.toString(), ONEDAY);
            break;
        }
    }

    @Override
    public Enumeration<String> getCurrentNames() {
        return request.getAttributeNames();
    }

    private HttpSession getSession() {
        if (session == null) {
            session = request.getSession();
        }
        return session;
    }

}

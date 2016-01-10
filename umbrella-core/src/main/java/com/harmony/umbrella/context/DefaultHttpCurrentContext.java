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
package com.harmony.umbrella.context;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class DefaultHttpCurrentContext implements HttpCurrentContext {

    private static final long serialVersionUID = -7923350971915932542L;

    private static final int SECONDS_OF_ONEDAY = 1000 * 60 * 60 * 24;

    protected final HttpServletRequest request;

    protected final HttpServletResponse response;

    protected Locale locale;

    private HttpSession session;

    private final Map<String, Object> currentMap = new HashMap<String, Object>();

    public DefaultHttpCurrentContext(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.session = request.getSession(false);
        this.put(HTTP_REQUEST, request);
        this.put(HTTP_RESPONSE, response);
        if (this.session != null) {
            this.put(HTTP_SESSION, session);
        }
    }

    @Override
    public Long getUserId() {
        return (Long) getSessionAttribute(USER_ID);
    }

    /**
     * 设置用户id, 一经设置就不再允许修改
     * 
     * @param userId
     *            用户id
     * @return if return {@code true}设置成功
     */
    public boolean setUserId(Long userId) {
        if (userId == null || containsSessionAttribute(USER_ID)) {
            return false;
        }
        setSessionAttribute(USER_ID, userId);
        return true;
    }

    @Override
    public String getUsername() {
        return (String) getSessionAttribute(USER_NAME);
    }

    @Override
    public String getNickname() {
        return (String) getSessionAttribute(USER_NICKNAME);
    }

    /**
     * 设置用户名称, 一经设置就不再允许修改
     */
    public boolean setUsername(String username) {
        if (StringUtils.isBlank(username) || containsSessionAttribute(USER_NAME)) {
            return false;
        }
        setSessionAttribute(USER_NAME, username);
        return true;
    }

    @Override
    public boolean isAuthenticated() {
        if (!sessionCreated()) {
            return false;
        }
        return getUserId() != null;
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
        this.locale = locale;
        this.response.setLocale(this.locale);
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
    public String getParameter(String name) {
        return request.getParameter(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAttribute(String name) {
        return (T) request.getAttribute(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getSessionAttribute(String name) {
        return (T) getHttpSession().getAttribute(name);
    }

    @Override
    public String getHttpCookie(String name) {
        for (Cookie cookie : getHttpCookies()) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    @Override
    public void setAttribute(String name, Object o) {
        request.setAttribute(name, o);
    }

    @Override
    public void setSessionAttribute(String name, Object o) {
        getHttpSession().setAttribute(name, o);
    }

    @Override
    public void addCookie(Cookie cookie) {
        response.addCookie(cookie);
    }

    public void addCookie(String name, String value) {
        addCookie(name, value, SECONDS_OF_ONEDAY);
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
        return getHttpCookie(name) != null;
    }

    @Override
    public boolean containsKey(String name) {
        return currentMap.containsKey(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String name) {
        return (T) currentMap.get(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String name, int scope) {
        Object result = null;
        switch (getScope(scope)) {
        case SCOPE_CURRENT:
            result = get(name);
            break;
        case SCOPE_REQUEST:
            result = getAttribute(name);
            break;
        case SCOPE_SESSION:
            result = getSessionAttribute(name);
            break;
        case SCOPE_COOKIE:
            result = getHttpCookie(name);
            break;
        }
        return (T) result;
    }

    /**
     * 一经设置不允许修改的属性有{@code HTTP_REQUEST}, {@code HTTP_RESPONSE},
     * {@code HTTP_SESSION}
     */
    @Override
    public void put(String name, Object o) {
        if ((HTTP_REQUEST.equals(name) || HTTP_RESPONSE.equals(name) || HTTP_SESSION.equals(name))) {
            if (!currentMap.containsKey(name)) {
                currentMap.put(name, o);
            }
        } else {
            currentMap.put(name, o);
        }
    }

    @Override
    public void put(String name, Object o, int scope) {
        switch (getScope(scope)) {
        case SCOPE_CURRENT:
            put(name, o);
            break;
        case SCOPE_REQUEST:
            setAttribute(name, o);
            break;
        case SCOPE_SESSION:
            setSessionAttribute(name, o);
            break;
        case SCOPE_COOKIE:
            addCookie(name, o == null ? null : o.toString(), SECONDS_OF_ONEDAY);
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
            put(HTTP_SESSION, session);
        }
        return session;
    }

    public Cookie[] getHttpCookies() {
        return request.getCookies();
    }

    private int getScope(int scope) {
        return scope <= SCOPE_CURRENT ? SCOPE_CURRENT : //
                scope <= SCOPE_REQUEST ? SCOPE_REQUEST : //
                        scope <= SCOPE_SESSION ? SCOPE_SESSION : SCOPE_COOKIE;
    }

}

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

import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author wuxii@foxmail.com
 */
public interface HttpCurrentContext extends CurrentContext {

    int SCOPE_REQUEST = 0;

    int SCOPE_SESSION = 100;

    int SCOPE_COOKIE = 200;

    <T> T get(String name, int scope);

    void put(String name, Object o, int scope);

    String getCharacterEncoding();

    Locale getLocale();

    void setLocale(Locale locale);

    HttpServletRequest getHttpRequest();

    HttpServletResponse getHttpResponse();

    boolean sessionCreated();

    HttpSession getHttpSession();

    String getSessionId();

    Object getAttribute(String name);

    Object getSessionAttribute(String name);

    Cookie[] getHttpCookies();

    String getParameter(String name);

    void setAttribute(String name, Object value);

    void setSessionAttribute(String name, Object value);

    void addCookie(Cookie cookie);

    void addCookie(String name, String value, int maxAge);

    boolean containsParameter(String name);

    boolean containsAttribute(String name);

    boolean containsSessionAttribute(String name);

    boolean containsCookie(String name);

}

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

import java.net.HttpCookie;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * http的用户操作上下文
 * <p/>
 * scope current< param < request < session < cookie
 *
 * @author wuxii@foxmail.com
 */
public interface HttpCurrentContext extends CurrentContext {

    /**
     * 值设置/获取的范围：current
     */
    int SCOPE_CURRENT = 1;// 1
    /**
     * 值设置/获取的范围：http-request/http-response
     */
    int SCOPE_REQUEST = SCOPE_CURRENT << 1; // 2
    /**
     * 值设置/获取的范围：http-session
     */
    int SCOPE_SESSION = SCOPE_CURRENT << 2;// 4
    /**
     * 值设置/获取的范围：http-cookie
     */
    int SCOPE_COOKIE = SCOPE_CURRENT << 3;// 8

    /**
     * 当前的字符集
     *
     * @return 字符集
     */
    String getCharacterEncoding();

    /**
     * 当前的本地化
     *
     * @return {@linkplain Locale}
     */
    Locale getLocale();

    /**
     * 设置用户环境的本地化
     *
     * @param locale
     *         {@linkplain Locale}
     */
    void setLocale(Locale locale);

    /**
     * 当前的http请求
     *
     * @return http-request
     */
    HttpServletRequest getHttpRequest();

    /**
     * 当前的http应答
     *
     * @return http-response
     */
    HttpServletResponse getHttpResponse();

    /**
     * 当前环境中是否已经创建了http-session
     *
     * @return if {@code true} has been created
     */
    boolean sessionCreated();

    /**
     * 获取当前的http-session
     *
     * @return http-session
     */
    HttpSession getHttpSession();

    /**
     * 获取session的id
     *
     * @return session id
     */
    String getSessionId();

    /**
     * 在当前环境中获取作用域为{@linkplain #SCOPE_CURRENT}的值
     */
    <T> T get(String name);

    /**
     * 在指定的范围获取值
     *
     * @param name
     *         key of value
     * @param scope
     *         作用域
     * @return if not exists return {@code null}
     */
    <T> T get(String name, int scope);

    /**
     * 获取request中的params
     *
     * @param name
     *         key of value
     * @return if not exists return {@code null}
     * @see javax.servlet.http.HttpServletRequest#getParameter(String)
     */
    String getParameter(String name);

    /**
     * 获取作用域为{@linkplain #SCOPE_REQUEST}的值
     *
     * @param name
     *         key of value
     * @return if not exists return {@code null}
     */
    <T> T getAttribute(String name);

    /**
     * 获取作用域为{@linkplain #SCOPE_SESSION}的值
     *
     * @param name
     *         key of value
     * @return if not exists return {@code null}
     */
    <T> T getSessionAttribute(String name);

    // Cookie[] getHttpCookies();

    /**
     * 获取作用域为{@linkplain #SCOPE_COOKIE}的值
     *
     * @param name
     *         key of value
     * @return if not exists return {@code null}
     */
    String getHttpCookie(String name);

    /**
     * 在当前的环境中设置作用域为{@linkplain #SCOPE_CURRENT}的值
     */
    void put(String name, Object o);

    /**
     * 在指定的范围设置值
     *
     * @param name
     *         key of value
     * @param o
     *         value
     * @param scope
     *         作用域
     */
    void put(String name, Object o, int scope);

    /**
     * 设置scope为{@linkplain #SCOPE_REQUEST}的值
     *
     * @param name
     *         key of value
     * @param value
     *         value
     */
    void setAttribute(String name, Object value);

    /**
     * 设置scope为{@linkplain #SCOPE_SESSION}的值
     *
     * @param name
     *         key of value
     * @param value
     *         value
     */
    void setSessionAttribute(String name, Object value);

    /**
     * 设置scope为{@linkplain #SCOPE_COOKIE}的值
     *
     * @param cookie
     *         {@linkplain HttpCookie}
     */
    void addCookie(Cookie cookie);

    /**
     * 设置scope为{@linkplain #SCOPE_COOKIE}的值，并指定最大生存时间{@code maxAge}
     *
     * @param name
     *         key of cookies
     * @param value
     *         value
     * @param maxAge
     *         max age
     */
    void addCookie(String name, String value, int maxAge);

    /**
     * 判断是否存在scope为{@linkplain #SCOPE_CURRENT}的key为{@code name}的值
     */
    boolean contains(String name);

    /**
     * 判断request中{@linkplain javax.servlet.http.HttpServletRequest#getParameter(java.lang.String)}是否存在对应值
     *
     * @param name
     *         key
     * @return {@code true} exists
     */
    boolean containsParameter(String name);

    /**
     * 判断是否存在scope为{@linkplain #SCOPE_REQUEST}的key为{@code name}的值
     *
     * @param name
     * @return
     */
    boolean containsAttribute(String name);

    /**
     * 判断是否存在scope为{@linkplain #SCOPE_SESSION}的key为{@code name}的值
     *
     * @param name
     * @return
     */
    boolean containsSessionAttribute(String name);

    /**
     * 判断是否存在scope为{@linkplain #SCOPE_COOKIE}的key为{@code name}的值
     *
     * @param name
     * @return
     */
    boolean containsCookie(String name);

    /**
     * 获取scope为{@linkplain #SCOPE_CURRENT}的所有key名称
     */
    Enumeration<String> getCurrentNames();

}

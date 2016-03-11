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
import java.util.Locale;

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
     * 设置用户环境的本地化
     *
     * @param locale
     *            {@linkplain Locale}
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
     * 在当前的环境中设置作用域为{@linkplain #SCOPE_CURRENT}的值
     */
    void put(String name, Object o);

    /**
     * 获取scope为{@linkplain #SCOPE_CURRENT}的所有key名称
     */
    Enumeration<String> getCurrentNames();

}

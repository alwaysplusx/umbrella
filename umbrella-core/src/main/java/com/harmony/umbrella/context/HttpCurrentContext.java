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
     * 当前的字符集
     *
     * @return 字符集
     */
    String getCharacterEncoding();

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

}

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

import java.io.Serializable;
import java.util.Enumeration;

/**
 * 用户所能操作信息以及用户的信息将会在保存在{@linkplain CurrentContext}中
 * 
 * @author wuxii@foxmail.com
 */
public interface CurrentContext extends Serializable {

    /**
     * key:客户端的地址
     */
    String REMOTE_HOST = CurrentContext.class.getName() + ".REMOTE_HOST";

    /**
     * key:用户id
     */
    String USER_ID = CurrentContext.class.getName() + ".USER_ID";

    /**
     * key:用户的名称
     */
    String USERNAME = CurrentContext.class.getName() + ".CURRENT_USERNAME";

    /**
     * key:用户的当前http请求
     */
    String HTTP_REQUEST = CurrentContext.class.getName() + ".HTTP_REQUEST";

    /**
     * key:用户的当前http应答
     */
    String HTTP_RESPONSE = CurrentContext.class.getName() + ".HTTP_RESPONSE";

    /**
     * key:用户的当前session
     */
    String HTTP_SESSION = CurrentContext.class.getName() + ".HTTP_SESSION";

    /**
     * 用户id
     * 
     * @return 用户id
     */
    Long getUserId();

    /**
     * 用户名
     * 
     * @return 用户名
     */
    String getUsername();

    /**
     * 用户的客户端地址
     * 
     * @return 用户的客户端地址
     */
    String getRemoteHost();

    /**
     * 当前是否是http发起的请求
     */
    boolean isHttpContext();

    /**
     * 用户上下文中是否包含对应的值
     * 
     * @param name
     *            key of value
     */
    boolean contains(String name);

    /**
     * 获取{@code name}对应的值， 如果不存在返回{@code null}
     * 
     * @param name
     *            key of value
     * @return if not exists return {@code null}
     */
    <T> T get(String name);

    /**
     * 对当前的用户环境设置值
     * 
     * @param name
     *            key of value
     * @param o
     *            value
     */
    void put(String name, Object o);

    /**
     * 当前用户环境中包含的key
     * 
     * @return 值的枚举类
     */
    Enumeration<String> getCurrentNames();

}

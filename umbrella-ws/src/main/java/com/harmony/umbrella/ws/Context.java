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
package com.harmony.umbrella.ws;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Map;

/**
 * 执行时候的交互上下文
 * <p/>
 * 上下文中包括: <ul> <li>待执行的服务接口 <li>待执行的方法 <li>调用地址 <li>用户名密码 <li>以及其他待扩展的属性 </ul>
 *
 * @author wuxii@foxmail.com
 */
public interface Context extends Metadata, Serializable {

    /**
     * 每次消息生成一个唯一键（ 使用数字自增，即创建一次+1）。
     *
     * @return 上下文的唯一标识
     */
    long getContextId();

    /**
     * 服务接口，对于客户端而言可以是CXF wsdl2java或wsimport生成的java类，服务端可以是服务接口
     *
     * @return 接口类
     */
    Class<?> getServiceInterface();

    /**
     * 方法参数
     *
     * @return 参数
     */
    Object[] getParameters();

    /**
     * 所要执行的方法
     *
     * @return 方法名
     */
    String getMethodName();

    /**
     * 访问地址
     *
     * @return 地址
     */
    String getAddress();

    /**
     * 用户名
     *
     * @return 用户名
     */
    String getUsername();

    /**
     * 用户密码
     *
     * @return 密码
     */
    String getPassword();

    /**
     * 在上下文中获取属性值
     *
     * @param contextKey
     *         检索键
     * @return 上下文中的值
     */
    Object get(String contextKey);

    /**
     * 上下文中是否包含检索条件
     *
     * @param key
     *         环境中的key
     * @return if contain return {@code true}
     */
    boolean contains(String key);

    /**
     * 所要执行的接口方法
     *
     * @return 接口方法
     * @throws NoSuchMethodException
     *         该接口中不存在该方法
     */
    Method getMethod() throws NoSuchMethodException;

    /**
     * 上下文中的其他属性
     *
     * @return 上下文所有的key
     */
    Enumeration<String> getContextNames();

    /**
     * 当前执行环境的Context
     *
     * @return Map 上下文中的内容
     */
    Map<String, Object> getContextMap();

    /**
     * 往当前环境中设置值
     *
     * @param key
     *         上下文中对应的key
     * @param value
     *         上下文对应的值
     */
    void put(String key, Object value);

}

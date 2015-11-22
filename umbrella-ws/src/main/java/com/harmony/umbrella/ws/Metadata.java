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

/**
 * web service 基础属性<p/>可将属性保存到数据库或者其他地方做的扩展
 *
 * @author wuxii@foxmail.com
 */
public interface Metadata {

    /**
     * 服务名
     *
     * @return 服务名
     */
    String getServiceName();

    /**
     * 服务接口类
     *
     * @return 服务接口类
     */
    Class<?> getServiceClass();

    /**
     * 服务所在的地址
     *
     * @return 服务地址
     */
    String getAddress();

    /**
     * 访问服务所需要使用的用户名. 非必须
     *
     * @return 用户名
     */
    String getUsername();

    /**
     * 范文服务所需要的密码. 非必须
     *
     * @return 密码
     */
    String getPassword();

    /**
     * 代理服务的连接超时时间
     *
     * @return 连接超时时间
     */
    long getConnectionTimeout();

    /**
     * 代理服务的接收超时时间
     *
     * @return 接收超时时间
     */
    long getReceiveTimeout();

    /**
     * 设置客户端等待时间
     *
     * @return 等待时间
     * @see com.harmony.umbrella.ws.jaxws.JaxWsProxyBuilder#setSynchronousTimeout(Object, int)
     */
    int getSynchronousTimeout();

}

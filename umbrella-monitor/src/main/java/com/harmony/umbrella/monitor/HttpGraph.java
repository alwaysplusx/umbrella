/*
 * Copyright 2002-2015 the original author or authors.
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
package com.harmony.umbrella.monitor;

/**
 * http监控结果，包含http请求的参数，url, 客户端地址等信息
 */
public interface HttpGraph extends Graph {

    /**
     * 监控{@link javax.servlet.http.HttpServletRequest}中对于的属性key
     */
    String HTTP_PROPERTY = HttpGraph.class.getName() + ".HTTP_PROPERTY";

    /**
     * 对应请求的http方法 GET, POST, PUT, DELETE, HEAD, OPTIONS, TRACE
     * 
     * @return Http的方法名称
     * @see javax.servlet.http.HttpServletRequest#getMethod()
     */
    String getHttpMethod();

    /**
     * 发起请求的地址
     * 
     * @return remote address
     * @see javax.servlet.http.HttpServletRequest#getRemoteAddr()
     */
    String getRemoteAddr();

    /**
     * 相应的地址
     * 
     * @return local server address
     * @see javax.servlet.http.HttpServletRequest#getLocalAddr()
     */
    String getLocalAddr();

    /**
     * 请求时候带的查询字符串
     * 
     * @return query string, url after '?'
     * @see javax.servlet.http.HttpServletRequest#getQueryString()
     */
    String getQueryString();

    /**
     * 应答的状态码
     * 
     * @return response code
     * @see javax.servlet.http.HttpServletResponse#getStatus()
     */
    int getStatus();

}
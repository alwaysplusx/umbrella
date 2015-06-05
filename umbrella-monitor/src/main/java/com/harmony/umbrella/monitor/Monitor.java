/*
 * Copyright 2013-2015 wuxii@foxmail.com.
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

import java.util.Collections;
import java.util.Set;

/**
 * 监控器，默认监控所有资源
 * 
 * @author wuxii@foxmail.com
 */
public interface Monitor<T> {

    /**
     * 查看监控策略 <p> default is {@linkplain MonitorPolicy#WhiteList}
     * 
     * @return
     */
    MonitorPolicy getPolicy();
    
    /**
     * 更改监控策略
     * 
     * @param policy
     * @see MonitorPolicy
     */
    void setPolicy(MonitorPolicy policy);

    /**
     * 排除监控一个资源路径模版
     * 
     * @param resourcePattern
     */
    void excludePattern(String pattern);

    /**
     * 监控一个资源模版
     * 
     * @param resourcePattern
     */
    void includePattern(String pattern);

    /**
     * 资源名单中移除资源
     * 
     * @param resource
     */
    void excludeResource(T resource);

    /**
     * 资源名单中添加资源
     * 
     * @param resource
     */
    void includeResource(T resource);

    /**
     * 检测该资源是否收到监控
     * 
     * @param resource
     * @return
     */
    boolean isMonitored(T resource);

    /**
     * 获取资源名单<p>资源的性质还要通过{@link #getPolicy()}来进一步定性 <ul> <li>
     * {@linkplain MonitorPolicy#WhiteList}表示为白名单资源 <li>
     * {@linkplain MonitorPolicy#BlockList}表示为黑名单资源<li>
     * {@linkplain MonitorPolicy#All}与{@linkplain MonitorPolicy#Skip}
     * 该资源名单无效</ul>
     * 
     * @return {@linkplain Collections#unmodifiableSet(Set)}
     */
    Set<T> getResources();

    /**
     * 监控模版名单
     * 
     * @return
     */
    Set<String> getPatterns();

    /**
     * 监控策略
     */
    public enum MonitorPolicy {
        /**
         * 监控所有资源<p>无视监控模版{@linkplain Monitor#getPatterns()}与资源名单
         * {@linkplain Monitor#getResources()}
         */
        All, //
        /**
         * 跳过所有监控<p>无视监控模版{@linkplain Monitor#getPatterns()}与资源名单
         * {@linkplain Monitor#getResources()}
         */
        Skip,
        /**
         * 监控模版{@linkplain Monitor#getPatterns()}内,
         * {@linkplain Monitor#getResources()}外的所有资源
         */
        WhiteList,
        /**
         * 监控模版{@linkplain Monitor#getPatterns()}内,
         * {@linkplain Monitor#getResources()}内的所有资源
         */
        BlockList
    }

}

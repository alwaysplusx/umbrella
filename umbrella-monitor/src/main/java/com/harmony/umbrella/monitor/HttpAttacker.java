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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.harmony.umbrella.monitor.annotation.HttpProperty.Scope;

/**
 * 对常用的Http内部资源获取做的扩展
 * 
 * @author wuxii@foxmail.com
 */
public interface HttpAttacker extends Attacker<HttpServletRequest> {

    /**
     * 根据scop以及属性的名称，获取Http内部的信息
     * <p>
     * {@linkplain Scope#PARAMETER}表示监控request中
     * {@linkplain HttpServletRequest#getParameter(String)}的值
     * 
     * @param request
     *            http请求
     * @param scope
     *            作用域
     * @param names
     *            需要获取的内部key
     * @return 所有key以及它的对应值组成的map
     * @see Scope
     */
    Map<String, Object> attack(HttpServletRequest request, Scope scope, String... names);

}

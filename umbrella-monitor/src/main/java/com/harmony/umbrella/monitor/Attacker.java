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
package com.harmony.umbrella.monitor;

import java.util.Map;

/**
 * 获取监控对象的内部属性
 * 
 * @author wuxii@foxmail.com
 */
public interface Attacker {

    /**
     * 获取内部数据
     * 
     * @param target
     *            目标监控对象
     * @param names
     *            内部对象名称. 如: 字段名称, 方法名称
     * @return 内部对象的键值对
     */
    Map<String, Object> attack(Object target, String... names);

}

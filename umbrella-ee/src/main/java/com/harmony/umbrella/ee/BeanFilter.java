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
package com.harmony.umbrella.ee;

/**
 * 对环境中查找到的bean进行过滤
 * 
 * @author wuxii@foxmail.com
 */
public interface BeanFilter {

    /**
     * 判断jndi对应的bean是否为所需要的bean
     * 
     * @param jndi
     *            jndi名称
     * @param bean
     *            bean实例
     */
    boolean accept(String jndi, Object bean);

}
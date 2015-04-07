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
package com.harmony.modules.core;

/**
 * bean加载
 * @author wuxii@foxmail.com
 */
public interface BeanLoader {

    /**
     * 单例
     */
    String SINGLETON = "singleton";

    /**
     * 原型
     */
    String PROTOTYPE = "prototype";

    /**
     * 根据bean的名称加载指定bean，默认获取单例的bean
     * @param beanName
     * @return
     */
    <T> T loadBean(String beanName);

    /**
     * 加载一个指定类型的bean
     * @param beanName
     * @param scope
     * @return
     */
    <T> T loadBean(String beanName, String scope);

    /**
     * 默认加载一个单例的bean
     * @param beanClass
     * @return
     */
    <T> T loadBean(Class<T> beanClass);

    /**
     * 加载一个指定类型的bean
     * @param beanClass
     * @param scope
     * @return
     */
    <T> T loadBean(Class<T> beanClass, String scope);

}

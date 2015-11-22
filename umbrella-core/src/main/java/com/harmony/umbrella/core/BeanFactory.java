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
package com.harmony.umbrella.core;

/**
 * bean加载
 *
 * @author wuxii@foxmail.com
 */
public interface BeanFactory {

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
     *
     * @param beanName
     *         需要获取的bean名称
     * @return 指定名称的bean
     */
    <T> T getBean(String beanName) throws NoSuchBeanFindException;

    /**
     * 加载一个指定类型的bean
     *
     * @param beanName
     *         需要获取的bean名称
     * @param scope
     *         bean scope
     * @return 指定名称的bean
     */
    <T> T getBean(String beanName, String scope) throws NoSuchBeanFindException;

    /**
     * 默认加载一个单例的bean
     *
     * @param beanClass
     *         需要获取的bean类
     * @return 指定类型的bean
     */
    <T> T getBean(Class<T> beanClass) throws NoSuchBeanFindException;

    /**
     * 加载一个指定类型的bean
     *
     * @param beanClass
     *         需要获取的bean类
     * @param scope
     *         bean scope
     * @return 指定类型的bean
     */
    <T> T getBean(Class<T> beanClass, String scope) throws NoSuchBeanFindException;

}

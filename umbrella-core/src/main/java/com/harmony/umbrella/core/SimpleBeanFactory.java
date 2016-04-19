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

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * 通过类的反射{@linkplain Class#newInstance()}来创建Bean
 * 
 * @author wuxii@foxmail.com
 */
public class SimpleBeanFactory implements BeanFactory, Serializable {

    private static final long serialVersionUID = 1L;

    public static final BeanFactory INSTANCE = new SimpleBeanFactory();

    private Map<Class<?>, Object> beans = new ConcurrentHashMap<Class<?>, Object>();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(String beanName) {
        try {
            Class<?> clazz = ClassUtils.forName(beanName);
            return (T) getBean(clazz, SINGLETON);
        } catch (ClassNotFoundException e) {
            throw new NoSuchBeanFoundException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(String beanName, String scope) {
        try {
            Class<?> clazz = ClassUtils.forName(beanName);
            return (T) getBean(clazz, scope);
        } catch (ClassNotFoundException e) {
            throw new NoSuchBeanFoundException(e.getMessage(), e);
        }
    }

    @Override
    public <T> T getBean(Class<T> beanClass) {
        return getBean(beanClass, SINGLETON);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(Class<T> beanClass, String scope) {
        if (SINGLETON.equals(scope)) {
            if (!beans.containsKey(beanClass)) {
                beans.put(beanClass, createBean(beanClass, null));
            }
            return (T) beans.get(beanClass);
        } else if (PROTOTYPE.equals(scope)) {
            return (T) createBean(beanClass, null);
        }
        throw new IllegalArgumentException("unsupport scope " + scope);
    }

    /**
     * 反射创建bean
     * 
     * @param beanClass
     * @param properties
     * @return
     */
    protected Object createBean(Class<?> beanClass, Map<String, Object> properties) {
        try {
            return ReflectionUtils.instantiateClass(beanClass);
        } catch (Exception e) {
            throw new NoSuchBeanFoundException(e.getMessage(), e);
        }
    }

}
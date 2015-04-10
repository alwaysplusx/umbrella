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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 通过类的反射{@linkplain Class#newInstance()}来创建Bean
 * 
 * @author wuxii@foxmail.com
 */
public class ClassBeanLoader implements BeanLoader, Serializable {

    private static final long serialVersionUID = 1L;
    private Map<Class<?>, Object> beans = new HashMap<Class<?>, Object>();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T loadBean(String beanName) {
        try {
            Class<?> clazz = Class.forName(beanName);
            return (T) loadBean(clazz, SINGLETON);
        } catch (ClassNotFoundException e) {
            throw new NoSuchBeanFindException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T loadBean(String beanName, String scope) {
        try {
            Class<?> clazz = Class.forName(beanName);
            return (T) loadBean(clazz, scope);
        } catch (ClassNotFoundException e) {
            throw new NoSuchBeanFindException(e.getMessage(), e);
        }
    }

    @Override
    public <T> T loadBean(Class<T> beanClass) {
        return loadBean(beanClass, SINGLETON);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T loadBean(Class<T> beanClass, String scope) {
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

    protected Object createBean(Class<?> beanClass, Map<String, Object> properties) {
        try {
            return beanClass.newInstance();
        } catch (Exception e) {
            throw new NoSuchBeanFindException(e.getMessage(), e);
        }
    }

}
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
package com.harmony.umbrella.config;

import java.lang.reflect.Method;
import java.util.List;

import com.harmony.umbrella.config.annotation.Bean;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ConfigSupport implements ConfigurationBeans, Configurations {

    private Method[] methods = getClass().getMethods();

    @SuppressWarnings("unchecked")
    @Override
    public final <T> T getBean(String beanName) {
        return (T) findBean(beanName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T> List<T> getBeans(String beanName) {
        return findBeans(beanName);
    }

    @Override
    public final <T> T getBean(Class<T> beanClass) {
        return getBean(beanClass.getName());
    }

    @Override
    public final <T> List<T> getBeans(Class<T> beanClass) {
        return getBeans(beanClass.getName());
    }

    private Object findBean(String beanName) {
        for (Method method : methods) {
            if (method.getParameterTypes().length == 0) {
                Bean ann = method.getAnnotation(Bean.class);
                if (ann != null) {
                    for (String v : ann.value()) {
                        if (v.equals(beanName)) {
                            return ReflectionUtils.invokeMethod(method, this);
                        }
                    }
                }
            }
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    private List findBeans(String beanName) {
        for (Method method : methods) {
            if (method.getParameterTypes().length == 0 && List.class.isAssignableFrom(method.getReturnType())) {
                Bean ann = method.getAnnotation(Bean.class);
                if (ann != null) {
                    for (String v : ann.value()) {
                        if (v.equals(beanName)) {
                            return (List) ReflectionUtils.invokeMethod(method, this);
                        }
                    }
                }
            }
        }
        return null;
    }
}

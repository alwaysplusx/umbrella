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

import java.util.Map;

/**
 * @author wuxii@foxmail.com
 */
public abstract class GeneralBeanLoader implements BeanLoader {

    @Override
    public <T> T loadBean(String beanName) throws NoSuchBeanFindException {
        return null;
    }

    @Override
    public <T> T loadBean(String beanName, String scope) throws NoSuchBeanFindException {
        return null;
    }

    @Override
    public <T> T loadBean(Class<T> beanClass) throws NoSuchBeanFindException {
        return null;
    }

    @Override
    public <T> T loadBean(Class<T> beanClass, String scope) throws NoSuchBeanFindException {
        return null;
    }

    protected abstract <T> T createBean(String beanName, Class<T> beanClass, Map<String, Object> props);

}

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
package com.harmony.umbrella.ws.cxf;

import org.apache.cxf.message.Message;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.NoSuchBeanFindException;
import com.harmony.umbrella.core.SimpleBeanFactory;
import com.harmony.umbrella.ws.jaxrs.JaxRsServerBuilder.BeanFactoryProvider;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleBeanFactoryProvider implements BeanFactoryProvider {

    private final Class<?> resourceClass;
    private boolean singleton;

    private BeanFactory beanFactory;

    public SimpleBeanFactoryProvider(Class<?> resourceClass) {
        this(resourceClass, new SimpleBeanFactory(), false);
    }

    public SimpleBeanFactoryProvider(Class<?> resourceClass, BeanFactory beanFactory) {
        this(resourceClass, beanFactory, false);
    }

    public SimpleBeanFactoryProvider(Class<?> resourceClass, BeanFactory beanFactory, boolean singleton) {
        this.resourceClass = resourceClass;
        this.beanFactory = beanFactory;
        this.singleton = singleton;
    }

    @Override
    public Object getInstance(Message m) {
        return getBean(resourceClass, isSingleton() ? SINGLETON : PROTOTYPE);
    }

    @Override
    public void releaseInstance(Message m, Object o) {
        // nothing
    }

    @Override
    public Class<?> getResourceClass() {
        return resourceClass;
    }

    @Override
    public boolean isSingleton() {
        return singleton;
    }

    @Override
    public <T> T getBean(String beanName) throws NoSuchBeanFindException {
        return beanFactory.getBean(beanName);
    }

    @Override
    public <T> T getBean(String beanName, String scope) throws NoSuchBeanFindException {
        return beanFactory.getBean(beanName, scope);
    }

    @Override
    public <T> T getBean(Class<T> beanClass) throws NoSuchBeanFindException {
        return beanFactory.getBean(beanClass);
    }

    @Override
    public <T> T getBean(Class<T> beanClass, String scope) throws NoSuchBeanFindException {
        return beanFactory.getBean(beanClass, scope);
    }

}

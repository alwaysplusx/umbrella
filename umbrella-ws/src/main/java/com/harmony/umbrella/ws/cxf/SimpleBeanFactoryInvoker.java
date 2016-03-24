/*
 * Copyright 2013-2015 wuxii@foxmail.com.
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

import org.apache.cxf.message.Exchange;
import org.apache.cxf.service.invoker.AbstractInvoker;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.NoSuchBeanFoundException;
import com.harmony.umbrella.core.SimpleBeanFactory;
import com.harmony.umbrella.ws.jaxws.JaxWsServerBuilder.BeanFactoryInvoker;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleBeanFactoryInvoker extends AbstractInvoker implements BeanFactoryInvoker {

    private static final Log log = Logs.getLog(SimpleBeanFactoryInvoker.class);
    protected final Class<?> serviceClass;
    private BeanFactory beanFactory;

    public SimpleBeanFactoryInvoker(Class<?> serviceClass) {
        this(serviceClass, new SimpleBeanFactory());
    }

    public SimpleBeanFactoryInvoker(Class<?> serviceClass, BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.serviceClass = serviceClass;
    }

    @Override
    public <T> T getBean(String beanName) throws NoSuchBeanFoundException {
        return beanFactory.getBean(beanName);
    }

    @Override
    public <T> T getBean(String beanName, String scope) throws NoSuchBeanFoundException {
        return beanFactory.getBean(beanName, scope);
    }

    @Override
    public <T> T getBean(Class<T> beanClass) throws NoSuchBeanFoundException {
        return beanFactory.getBean(beanClass);
    }

    @Override
    public <T> T getBean(Class<T> beanClass, String scope) throws NoSuchBeanFoundException {
        return beanFactory.getBean(beanClass, scope);
    }

    @Override
    public Object getServiceObject(Exchange context) {
        log.debug("get instance [{}({})] from [{}]", serviceClass.getName(), PROTOTYPE, beanFactory);
        return getBean(serviceClass, PROTOTYPE);
    }

}

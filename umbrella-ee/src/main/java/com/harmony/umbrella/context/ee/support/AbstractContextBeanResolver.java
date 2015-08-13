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
package com.harmony.umbrella.context.ee.support;

import javax.naming.Context;

import com.harmony.umbrella.context.ee.BeanDefinition;
import com.harmony.umbrella.context.ee.ContextBeanResolver;
import com.harmony.umbrella.context.ee.SessionBean;
import com.harmony.umbrella.context.ee.WrappedBeanHandler;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractContextBeanResolver extends ConfigurationBeanResolver implements ContextBeanResolver {

    protected abstract SessionBean deepSearch(BeanDefinition beanDefinition, Context context);

    @Override
    public SessionBean search(final BeanDefinition beanDefinition, Context context) {
        final SimpleSessionBean result = new SimpleSessionBean(beanDefinition);
        Object bean = null;

        if (!filter(beanDefinition)) {

            bean = guessBean(beanDefinition, context, new SessionBeanAccept() {

                @Override
                public boolean accept(String jndi, Object bean) {
                    for (WrappedBeanHandler handler : getWrappedBeanHandlers()) {
                        if (handler.isWrappedBean(bean)) {
                            bean = handler.unwrap(bean);
                            result.wrapped = true;
                            break;
                        }
                    }
                    Class<?> remoteClass = beanDefinition.getSuitableRemoteClass();
                    if (beanDefinition.getBeanClass().isInstance(bean) //
                            || (remoteClass != null && remoteClass.isInstance(bean))) {
                        result.bean = bean;
                        result.jndi = jndi;
                        return true;
                    }
                    return false;
                }

            });
        }

        return bean == null ? deepSearch(beanDefinition, context) : result;
    }

    protected boolean filter(BeanDefinition beanDefinition) {
        return !(beanDefinition.isRemoteClass() || beanDefinition.isSessionBean() || beanDefinition.isLocalClass());
    }
}

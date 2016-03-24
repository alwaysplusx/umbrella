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
package com.harmony.umbrella.context.ee;

import java.lang.reflect.Field;
import java.util.Properties;

import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.harmony.umbrella.context.ee.BeanFilter;
import com.harmony.umbrella.context.ee.resolver.InternalContextResolver;
import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.NoSuchBeanFoundException;
import com.harmony.umbrella.util.ClassUtils.ClassFilterFeature;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class EJBBeanFactory implements BeanFactory {

    private ContextResolver contextResolver;
    private final Properties props = new Properties();

    public EJBBeanFactory(Properties props) {
        this(new InternalContextResolver(props), props);
    }

    public EJBBeanFactory(ContextResolver contextResolver, Properties props) {
        this.contextResolver = contextResolver;
        this.props.putAll(props);
    }

    @Override
    public <T> T getBean(String beanName) throws NoSuchBeanFoundException {
        return getBean(beanName, BeanFactory.SINGLETON);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(String beanName, String scope) throws NoSuchBeanFoundException {
        Context context = getContext();
        Object bean = contextResolver.tryLookup(beanName, context);
        if (bean == null) {
            try {
                bean = getBean(Class.forName(beanName), scope);
            } catch (ClassNotFoundException e) {
            }
        }
        return (T) bean;
    }

    @Override
    public <T> T getBean(Class<T> beanClass) throws NoSuchBeanFoundException {
        return getBean(beanClass, BeanFactory.SINGLETON);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(Class<T> beanClass, String scope) throws NoSuchBeanFoundException {
        Object bean = null;
        final BeanDefinition bd = new BeanDefinition(beanClass);
        Context context = getContext();
        bean = contextResolver.guessBean(bd, context, new BeanFilter() {

            @Override
            public boolean accept(String jndi, Object bean) {
                return contextResolver.isDeclareBean(bd, bean);
            }
        });
        if (bean == null) {
            bean = contextResolver.search(bd, context);
        }
        if (bean == null && ClassFilterFeature.NEWABLE.accept(beanClass)) {
            bean = ReflectionUtils.instantiateClass(beanClass);
            fillReferenceBean(bean, beanClass);
        } else {
            throw new NoSuchBeanFoundException("can't find bean of " + beanClass);
        }
        return (T) bean;
    }

    private void fillReferenceBean(Object bean, Class<?> beanClass) {
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            EJB ann = field.getAnnotation(EJB.class);
            if (ann != null) {
                BeanDefinition beanDefinition = null;
                String mappedName = ann.mappedName();
                if (StringUtils.isBlank(mappedName)) {
                    beanDefinition = new BeanDefinition(beanClass, mappedName);
                } else {
                    beanDefinition = new BeanDefinition(beanClass);
                }
                SessionBean sessionBean = contextResolver.search(beanDefinition, getContext());
                if (sessionBean == null) {
                    throw new IllegalArgumentException("cannot get reference bean of " + field.getName());
                }
            }
        }
    }

    private Context getContext() {
        try {
            return new InitialContext(props);
        } catch (NamingException e) {
            throw new IllegalStateException(e);
        }
    }

    public ContextResolver getContextResolver() {
        return contextResolver;
    }

    public void setContextResolver(ContextResolver contextResolver) {
        this.contextResolver = contextResolver;
    }

}

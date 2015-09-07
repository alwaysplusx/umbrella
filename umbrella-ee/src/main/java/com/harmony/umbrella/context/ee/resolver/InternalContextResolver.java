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
package com.harmony.umbrella.context.ee.resolver;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.context.ee.BeanDefinition;
import com.harmony.umbrella.context.ee.ContextResolver;
import com.harmony.umbrella.context.ee.SessionBean;

/**
 * @author wuxii@foxmail.com
 */
public class InternalContextResolver extends ConfigurationBeanResolver implements ContextResolver {

    private static final Logger log = LoggerFactory.getLogger(InternalContextResolver.class);
    private final Set<String> roots = new HashSet<String>();
    private final int deeps;

    public InternalContextResolver(Properties props) {
        super(props);
        this.deeps = Integer.valueOf(props.getProperty("jndi.search.deeps", "10"));
        this.roots.addAll(fromProps(props, "jndi.context.root"));
    }

    protected SimpleSessionBean deepSearch(SimpleSessionBean bean, Context context) {
        for (String root : roots) {
            doDeepSearch(root, bean, context, 0);
            if (bean.bean != null && bean.jndi != null) {
                return bean;
            }
        }
        return null;
    }

    private void doDeepSearch(String root, SimpleSessionBean sessionBean, Context context, int deeps) {
        if (deeps > this.deeps) {
            return;
        }
        log.info("deep search in context [{}], deep index {}", root, deeps);
        BeanDefinition beanDefinition = sessionBean.getBeanDefinition();
        try {
            Object bean = context.lookup(root);
            if (bean instanceof Context) {
                NamingEnumeration<NameClassPair> subCtxs = ((Context) bean).list("");
                while (subCtxs.hasMoreElements()) {
                    NameClassPair subNcp = subCtxs.nextElement();
                    String subRoot = root + ("".equals(root) ? "" : "/") + subNcp.getName();
                    doDeepSearch(subRoot, sessionBean, context, deeps++);
                    if (sessionBean.bean != null && sessionBean.jndi != null) {
                        return;
                    }
                }
            } else {
                Object unwrapBean = unwrap(bean);
                if (isDeclare(beanDefinition, unwrapBean)) {
                    sessionBean.bean = bean;
                    sessionBean.jndi = root;
                    sessionBean.wrapped = bean != unwrapBean;
                    return;
                }
            }
        } catch (NamingException e) {
            log.warn("context [{}] not find in {}", root, context);
        }
    }

    @Override
    public SessionBean search(final BeanDefinition beanDefinition, Context context) {
        final SimpleSessionBean result = new SimpleSessionBean(beanDefinition);
        Object bean = null;

        if (!filter(beanDefinition)) {
            bean = guessBean(beanDefinition, context, new BeanFilter() {

                @Override
                public boolean accept(String jndi, Object bean) {
                    Object unwrapBean = unwrap(bean);
                    if (isDeclare(beanDefinition, unwrapBean)) {
                        result.bean = bean;
                        result.jndi = jndi;
                        result.wrapped = bean != unwrapBean;
                        return true;
                    }
                    return false;
                }

            });
        }

        return bean == null ? deepSearch(result, context) : result;
    }

    protected boolean filter(BeanDefinition beanDefinition) {
        return !(beanDefinition.isRemoteClass() //
                || beanDefinition.isSessionBean() //
                || beanDefinition.isLocalClass() //
        || beanDefinition.getBeanClass().isInterface());
    }

}

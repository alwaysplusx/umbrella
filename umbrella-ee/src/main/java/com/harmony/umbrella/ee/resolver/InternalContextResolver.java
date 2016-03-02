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
package com.harmony.umbrella.ee.resolver;

import static com.harmony.umbrella.ee.util.TextMatchCalculator.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

import com.harmony.umbrella.ee.BeanDefinition;
import com.harmony.umbrella.ee.BeanFilter;
import com.harmony.umbrella.ee.ContextResolver;
import com.harmony.umbrella.ee.SessionBean;
import com.harmony.umbrella.util.ClassUtils;

/**
 * JavaEE环境内部解析工具，用于分析{@linkplain javax.naming.Context}
 *
 * @author wuxii@foxmail.com
 */
public class InternalContextResolver extends ConfigurationBeanResolver implements ContextResolver {

    private static final Log log = Logs.getLog(InternalContextResolver.class);
    // class - jndi
    private final Map<Class<?>, Set<String>> fastCache = new HashMap<Class<?>, Set<String>>();
    private final Set<String> roots = new HashSet<String>();
    private final int deeps;

    public InternalContextResolver(Properties props) {
        super(props);
        this.deeps = Integer.valueOf(props.getProperty("jndi.search.deeps", "10"));
        this.roots.addAll(fromProps(props, "jndi.context.root"));
    }

    /**
     * 深度查找BeanDefinition对应的类以及jndi
     */
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
        log.debug("deep search in context [{}], deep index {}", root, deeps);
        BeanDefinition beanDefinition = sessionBean.getBeanDefinition();
        try {
            Object bean = context.lookup(root);
            if (bean instanceof Context) {
                NamingEnumeration<NameClassPair> subCtxs = ((Context) bean).list("");
                while (subCtxs.hasMoreElements()) {
                    NameClassPair subNcp = subCtxs.nextElement();
                    // 迭代查找
                    doDeepSearch(toJndi(root, subNcp), sessionBean, context, deeps++);
                    if (sessionBean.bean != null && sessionBean.jndi != null) {
                        return;
                    }
                }
            } else {
                recordBeanWithJndi(beanDefinition, bean, root);
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

    /**
     * root + NameClassPair 组合生成下一个jndi名称
     */
    private String toJndi(String root, NameClassPair subNcp) {
        // root + ("".equals(root) ? "" : "/") + subNcp.getName();
        StringBuilder jndi = new StringBuilder(root);
        if (!"".equals(root)) {
            jndi.append("/");
        }
        jndi.append(subNcp.getName());
        return jndi.toString();
    }

    /**
     * 记录jndi以及bean到缓存中
     */
    private void recordBeanWithJndi(BeanDefinition bd, Object bean, String root) {
        Class<? extends Object> beanClass = bean.getClass();
        Class<?>[] interfaces = ClassUtils.getAllInterfaces(beanClass);
        for (Class<?> interfce : interfaces) {
            String className = interfce.getName();
            if (!className.startsWith("java.") //
                    && !className.startsWith("javax.")//
                    && !className.startsWith("org.apache.") //
                    && !className.startsWith("com.weblogic") //
                    && !className.startsWith("org.glassfish")//
                    && !className.startsWith("org.jboss")) {
                putIntoCache(interfce, root);
            }
        }
    }

    @Override
    public void clear() {
        fastCache.clear();
    }

    /**
     * 将 interface 以及对应的jndi到缓存中
     */
    private void putIntoCache(Class<?> interfce, String root) {
        Set<String> jndis = fastCache.get(interfce);
        if (jndis == null) {
            synchronized (fastCache) {
                if (!fastCache.containsKey(interfce)) {
                    jndis = new HashSet<String>();
                    fastCache.put(interfce, jndis);
                }
            }
        }
        jndis.add(root);
    }

    @Override
    public SessionBean search(final BeanDefinition beanDefinition, Context context) {
        final SimpleSessionBean result = new SimpleSessionBean(beanDefinition);
        Class<?> beanClass = beanDefinition.getBeanClass();
        Object bean = null;

        Set<String> jndis = fastCache.get(beanClass);
        if (jndis != null) {
            double currentRatio = 0;
            for (String jndi : jndis) {
                bean = tryLookup(jndi, context);
                Object unwrapBean = unwrap(bean);
                if (isDeclare(beanDefinition, bean)) {
                    double ratio = matchingRate(jndi, beanClass.getName());
                    if (ratio >= currentRatio) {
                        currentRatio = ratio;
                        result.bean = bean;
                        result.jndi = jndi;
                        result.wrapped = bean != unwrapBean;
                    }
                }
            }
            if (result.bean != null && result.jndi != null) {
                return result;
            }
        }

        if (!filter(beanDefinition)) {
            bean = guessBean(beanDefinition, context, new BeanFilter() {
                @Override
                public boolean accept(String jndi, Object bean) {
                    recordBeanWithJndi(beanDefinition, bean, jndi);
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

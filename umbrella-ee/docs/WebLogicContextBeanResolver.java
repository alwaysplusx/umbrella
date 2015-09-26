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
package com.harmony.umbrella.context.ee.resolver.wls;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.context.ee.BeanDefinition;
import com.harmony.umbrella.context.ee.ContextBean;
import com.harmony.umbrella.context.ee.WrappedBeanHandler;
import com.harmony.umbrella.context.ee.impl.ContextBeanImpl;
import com.harmony.umbrella.context.ee.resolver.AbstractContextBeanResolver;

/**
 * @author wuxii@foxmail.com
 */
public class WebLogicContextBeanResolver extends AbstractContextBeanResolver {

    private static final Logger log = LoggerFactory.getLogger(WebLogicContextBeanResolver.class);
    private static final Set<WrappedBeanHandler> handlers = new HashSet<WrappedBeanHandler>();
    private long waitTime;
    private static final AtomicLong searchThreadCount = new AtomicLong();

    static {
        handlers.add(new WrappedBeanHandler() {

            @Override
            public Object unwrap(Object bean) {
                try {
                    return bean.getClass().getMethod("getBean").invoke(bean);
                } catch (Exception e) {
                }
                return null;
            }

            @Override
            public boolean matches(Class<?> beanClass) {
                try {
                    Class<?> clazz = Class.forName("weblogic.ejb.container.internal.SessionEJBContextImpl");
                    return clazz.isAssignableFrom(beanClass);
                } catch (ClassNotFoundException e) {
                    return false;
                }
            }
        });
    }

    public WebLogicContextBeanResolver(Properties props) {
        this(props, Long.parseLong(props.getProperty("jndi.search.timeout", "10000")));
    }

    public WebLogicContextBeanResolver(Properties props, long waitTime) {
        super(props);
        this.waitTime = waitTime;
    }

    @Override
    protected ContextBean deepSearch(final Context context, final String root, final BeanDefinition beanDefinition) {
        final WrapInfo wrapResult = new WrapInfo();
        // 创建查找线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (wrapResult.lockObject) {
                    try {
                        deepSearch0(context, root, beanDefinition, wrapResult);
                    } finally {
                        wrapResult.interrupted = true;
                        wrapResult.lockObject.notify();
                    }
                }
                log.info("Thread of {} destory", Thread.currentThread().getName());
            }
        }, "Search-Thread-" + searchThreadCount.getAndIncrement()).start();

        long start = System.currentTimeMillis();
        boolean timeout = System.currentTimeMillis() - start > waitTime;

        synchronized (wrapResult.lockObject) {
            try {
                while (wrapResult.bean == null && !timeout && !wrapResult.interrupted) {
                    wrapResult.lockObject.notify();
                    wrapResult.lockObject.wait();
                    timeout = System.currentTimeMillis() - start > waitTime;
                    log.debug("parent thread has been notify, current context root[{}]", root);
                }
            } catch (InterruptedException e) {
                return null;
            } finally {
                wrapResult.interrupted = true;
                wrapResult.lockObject.notify();
            }
        }

        log.info("stop wait search thread, found[{}], timeout[{}]", wrapResult.bean != null, timeout);
        return wrapResult.bean;
    }

    private void deepSearch0(Context context, String root, BeanDefinition beanDefinition, WrapInfo wrap) {
        try {
            wrap.lockObject.notify();
            wrap.lockObject.wait();
            log.debug("search thread has been notify, search context[{}]", root);
            if (wrap.interrupted || wrap.bean != null) {// 中断或者已经找到则无需再查找
                log.info("search thread on context[{}] found bean[{}]/interrupted[{}]", root, wrap.bean, wrap.interrupted);
                return;
            }
            try {
                Object obj = context.lookup(root);
                if (isDeclareBean(beanDefinition, obj)) {
                    wrap.bean = new ContextBeanImpl(beanDefinition, root, unwrap(obj), isWrappedBean(obj));
                }
                if (obj instanceof Context) {
                    NamingEnumeration<NameClassPair> subCtxs = ((Context) obj).list("");
                    while (subCtxs.hasMoreElements()) {
                        NameClassPair subNcp = subCtxs.nextElement();
                        String subJndi = root + ("".equals(root) ? "" : "/") + subNcp.getName();
                        deepSearch0(context, subJndi, beanDefinition, wrap);
                    }
                }
            } catch (NamingException e) {
            }
        } catch (InterruptedException e) {
            wrap.interrupted = true;
        }
    }

    @Override
    protected boolean isWrappedBean(Object object) {
        for (WrappedBeanHandler handler : handlers) {
            if (handler.matches(object.getClass())) {
                return true;
            }
        }
        return super.isWrappedBean(object);
    }

    @Override
    public Object unwrap(Object bean) {
        for (WrappedBeanHandler handler : handlers) {
            if (handler.matches(bean.getClass())) {
                return handler.unwrap(bean);
            }
        }
        return super.unwrap(bean);
    }

    private static class WrapInfo {
        private final Object lockObject = new Object();
        private ContextBean bean;
        private boolean interrupted;
    }

}

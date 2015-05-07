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
package com.harmony.umbrella.context.ee.resolver;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import com.harmony.umbrella.context.ee.BeanDefinition;
import com.harmony.umbrella.context.ee.ContextBean;
import com.harmony.umbrella.context.ee.impl.ContextBeanImpl;

/**
 * @author wuxii@foxmail.com
 */
public class GenericContextBeanResolver extends AbstractContextBeanResolver {

    public GenericContextBeanResolver(Properties props) {
        super(props);
    }

    protected ContextBean deepSearch(Context context, String root, BeanDefinition beanDefinition) {
        LOG.debug("deep search context [{}]", root);
        try {
            Object obj = context.lookup(root);
            if (isDeclareBean(beanDefinition, obj)) {
                return new ContextBeanImpl(beanDefinition, root, unwrap(obj), isWrappedBean(obj));
            }
            if (obj instanceof Context) {
                NamingEnumeration<NameClassPair> subCtxs = ((Context) obj).list("");
                while (subCtxs.hasMoreElements()) {
                    NameClassPair subNcp = subCtxs.nextElement();
                    String subJndi = root + ("".equals(root) ? "" : "/") + subNcp.getName();
                    ContextBean contextBean = deepSearch(context, subJndi, beanDefinition);
                    if (contextBean != null) {
                        return contextBean;
                    }
                }
            }
        } catch (NamingException e) {
            LOG.debug("", e);
        }
        return null;
    }

}

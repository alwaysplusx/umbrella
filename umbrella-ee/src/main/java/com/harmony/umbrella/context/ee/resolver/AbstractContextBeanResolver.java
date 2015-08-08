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
package com.harmony.umbrella.context.ee.resolver;

import java.util.Properties;

import javax.naming.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.context.ee.BeanDefinition;
import com.harmony.umbrella.context.ee.ContextBean;
import com.harmony.umbrella.context.ee.BeanResolver;
import com.harmony.umbrella.context.ee.GenericContextResolver;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractContextBeanResolver extends GenericContextResolver implements BeanResolver {

    protected static final Logger LOG = LoggerFactory.getLogger(AbstractContextBeanResolver.class);

    protected static final String contextRoot = "java:";

    /**
     * javaEE环境开始的上下文根
     */
    protected String[] jndiContextRoot;

    public AbstractContextBeanResolver(Properties props) {
        super(props);
        String[] roots = props.getProperty("jndi.context.root", contextRoot).split(",");
        this.jndiContextRoot = new String[roots.length];
        for (int i = 0, max = roots.length; i < max; i++) {
            jndiContextRoot[i] = roots[i].trim();
        }
    }

    @Override
    public ContextBean search(Context context, BeanDefinition beanDefinition) {
        ContextBean bean = null;
        if (beanDefinition.isSessionBean() || beanDefinition.isRemoteClass() || beanDefinition.isLocalClass()) {
            bean = doSearch(context, beanDefinition);
        } else {
            LOG.info("sikp search, beanDefinition[{}] is not session/remote/local ejb class", beanDefinition);
        }
        return bean;
    }

    protected ContextBean doSearch(Context context, BeanDefinition beanDefinition) {
        ContextBean result = null;
        for (String root : jndiContextRoot) {
            result = deepSearch(context, root, beanDefinition);
            if (result != null) {
                LOG.debug("find bean[{}] in context[{}], bean {}", beanDefinition.getBeanClass(), result.getJndi(), result.getBean());
                break;
            }
        }
        return result;
    }

    /**
     * 迭代当前{@code context}查找目标类型的bean描述
     * 
     * @param context
     *            上下文
     * @param root
     *            查找的起点
     * @param beanDefinition
     *            目标bean类型
     * @return 目标bean
     */
    protected abstract ContextBean deepSearch(Context context, String root, BeanDefinition beanDefinition);

}

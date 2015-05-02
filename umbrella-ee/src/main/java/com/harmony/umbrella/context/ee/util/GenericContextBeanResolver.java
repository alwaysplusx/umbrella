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
package com.harmony.umbrella.context.ee.util;

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
public class GenericContextBeanResolver extends GenericContextResolver implements ContextBeanResolver {

	protected static final String contextRoot = "java:";

	/**
	 * javaEE环境开始的上下文根
	 */
	private String[] jndiContextRoot;

	public GenericContextBeanResolver(Properties props) {
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
			for (String root : jndiContextRoot) {
				bean = deepSearch(context, root, beanDefinition);
				if (bean != null) {
					log.debug("find bean[{}] in context[{}], bean {}", beanDefinition.getBeanClass(), bean.getJndi(), bean.getBean());
					break;
				}
			}
		}
		return bean;
	}

	protected ContextBean deepSearch(Context context, String root, BeanDefinition beanDefinition) {
		log.debug("deep search context [{}]", root);
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
		}
		return null;
	}

	protected boolean isDeepSearchBean(Object bean) {
		return true;
	}

}

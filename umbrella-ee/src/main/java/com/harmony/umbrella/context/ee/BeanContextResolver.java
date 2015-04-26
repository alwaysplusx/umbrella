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
package com.harmony.umbrella.context.ee;

/**
 * JavaEE 环境中的内容解析<p>在不同的EE环境中需要不同的方式解决jndi名称问题。 以及bean与声明的{@linkplain BeanDefinition}匹配选择
 * 
 * @author wuxii@foxmail.com
 */
public interface BeanContextResolver {

	String BEAN_JNDI_SEPARATOR = "#";

	String SUFFIX_BEAN = "Bean";
	String SUFFIX_REMOTE = "Remote";
	String SUFFIX_LOCAL = "Local";

	/**
	 * 格式化jndi名称
	 * 
	 * @param beanDefinition
	 * @return
	 */
	String resolveBeanName(BeanDefinition beanDefinition);

	/**
	 * 查看bean是否与声明的类型匹配
	 * 
	 * @param declaer
	 * @param bean
	 * @return
	 */
	boolean isDeclareBean(BeanDefinition declaer, Object bean);

}

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

import javax.naming.Context;

/**
 * @author wuxii@foxmail.com
 */
public interface BeanResolver extends ContextResolver {

	/**
	 * 在context中查找指定类型的bean
	 * 
	 * @param beanDefinition
	 *            bean的描述
	 * @return 如果没有找到返回{@code null}
	 */
	ContextBean search(Context context, BeanDefinition beanDefinition);

	// /**
	// * context中查找bean
	// *
	// * @param clazz
	// * bean的类型
	// * @param mappedName
	// * bean的映射名
	// * @return 如果没有找到返回{@code null}
	// */
	// ContextBean lookup(Context context, Class<?> clazz, String mappedName);

}

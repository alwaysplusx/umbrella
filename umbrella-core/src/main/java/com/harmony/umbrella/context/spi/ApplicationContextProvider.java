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
package com.harmony.umbrella.context.spi;

import com.harmony.umbrella.context.ApplicationContext;

/**
 * 应用上下文的provider. 让应用可以在使用时候选择创建何种的应用环境
 * 
 * @author wuxii@foxmail.com
 */
public interface ApplicationContextProvider {

	/**
	 * 创建应用上下文
	 * 
	 * @return 应用上下文
	 */
	ApplicationContext createApplicationContext();

}

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
package com.harmony.umbrella.context.ee.jmx;

/**
 * @author wuxii@foxmail.com
 */
public interface EJBContextMBean {

	/**
	 * 加载属性文件地址为jndi.properties <p> 如果该配置文件已经加载则通过<code>mandatory=true</code>起到强制加载作用
	 * 
	 * @param mandatory
	 *            是否强制加载标志
	 */
	void loadProperties(boolean mandatory);

	/**
	 * 清除已经加载的属性
	 */
	void cleanProperties();

	/**
	 * 当前上下文属性文件所在位置
	 * 
	 * @return
	 */
	String jndiPropertiesFilePath();

}

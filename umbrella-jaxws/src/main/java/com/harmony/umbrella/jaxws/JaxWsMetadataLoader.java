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
package com.harmony.umbrella.jaxws;

/**
 * 
 * 加载服务的元数据信息<p> [{@linkplain #getAddress(Class)},
 * {@linkplain #getPassword(Class)}, {@linkplain #getUsername(Class)}]可用
 * {@linkplain #getJaxWsMetadata(Class)}替代
 * 
 * @author wuxii@foxmail.com
 */
public interface JaxWsMetadataLoader {

	/**
	 * 根据指定的serviceClass加载指定的元数据
	 * 
	 * @param serviceClass
	 * @return
	 */
	JaxWsMetadata getJaxWsMetadata(Class<?> serviceClass);

	// JaxWsMetadata getJaxWsMetadata(String serviceName);

	/**
	 * 获取指定serviceClass的用户名
	 * 
	 * @param serviceClass
	 * @return
	 */
	String getUsername(Class<?> serviceClass);

	/**
	 * 获取指定serviceClass的密码
	 * 
	 * @param serviceClass
	 * @return
	 */
	String getPassword(Class<?> serviceClass);

	/**
	 * 获取指定serviceClass的地址
	 * 
	 * @param serviceClass
	 * @return
	 */
	String getAddress(Class<?> serviceClass);

}

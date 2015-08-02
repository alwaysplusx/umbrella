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
package com.harmony.umbrella.mapper;

/**
 * 标识可映射的对象
 * 
 * @author wuxii@foxmail.com
 */
public interface Mappable {

	/**
	 * 映射的名称，Id作用
	 * 
	 * @return
	 */
	String getMappedName();

	/**
	 * 映射的类
	 * 
	 * @return
	 */
	Class<?> getMappedClass();

}

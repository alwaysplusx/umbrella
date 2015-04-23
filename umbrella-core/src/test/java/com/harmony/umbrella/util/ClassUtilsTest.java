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
package com.harmony.umbrella.util;

import java.io.Serializable;

import org.junit.Test;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.core.SimpleBeanFactory;

/**
 * @author wuxii@foxmail.com
 */
public class ClassUtilsTest extends SimpleBeanFactory implements Serializable, App {

	private static final long serialVersionUID = 3038745586834292178L;

	@Test
	public void test() {
		for (Class<?> claz : ClassUtils.getAllInterfaces(getClass())) {
			System.out.println(claz);
		}
	}

	@Override
	public ApplicationContext createApplicationContext() {
		return null;
	}

}

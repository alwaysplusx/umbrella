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

import static org.junit.Assert.*;

import org.junit.Test;

import com.harmony.umbrella.context.bean.JeeSessionBean;
import com.harmony.umbrella.context.bean.JeeSessionRemote;

/**
 * @author wuxii@foxmail.com
 */
public class BeanDefinitionTest {

	@Test
	public void testCreateBeanDefinition() {
		BeanDefinition bd = new BeanDefinition(JeeSessionBean.class);
		assertEquals("JeeBean", bd.getName());
		assertEquals("JeeSessionBean", bd.getMappedName());
		assertEquals("java environment bean", bd.getDescription());
		assertTrue(bd.isSessionBean());
		assertTrue(bd.hasRemoteClass());
		assertFalse(bd.hasLocalClass());
		assertTrue(bd.isStateless());
		assertEquals(JeeSessionRemote.class, bd.getRemoteClass()[0]);
	}

}

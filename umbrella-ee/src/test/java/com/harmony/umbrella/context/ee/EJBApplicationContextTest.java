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

import javax.ejb.embeddable.EJBContainer;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.bean.JeeSessionBean;
import com.harmony.umbrella.context.bean.JeeSessionRemote;

/**
 * @author wuxii@foxmail.com
 */
public class EJBApplicationContextTest {

	private static EJBContainer container;

	@BeforeClass
	public static void setUp() {
		container = EJBContainer.createEJBContainer();
	}

	@Test
	public void test() {
		ApplicationContext context = EJBApplicationContext.getInstance();
		JeeSessionRemote bean = context.getBean(JeeSessionBean.class);
		assertNotNull(bean);
	}

	@AfterClass
	public static void tearDown() {
		container.close();
	}

}

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
package com.harmony.umbrella.context;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
public class JeeContextTest {

	private static EJBContainer container;

	@BeforeClass
	public static void setUp() {
		container = EJBContainer.createEJBContainer();
	}

	@Test
	public void test() throws Exception {
		Context context = container.getContext();
		Context rootContext = (Context) context.lookup("java:");
		NamingEnumeration<NameClassPair> ncps = rootContext.list("");
		while (ncps.hasMoreElements()) {
			NameClassPair ncp = ncps.next();
			String className = ncp.getClassName();
			String name = ncp.getName();
			String nameInNamespace = "";// ncp.getNameInNamespace();
			System.err.println("className=" + className + ", name=" + name + ", nameInNamespace=" + nameInNamespace);
		}
	}

}

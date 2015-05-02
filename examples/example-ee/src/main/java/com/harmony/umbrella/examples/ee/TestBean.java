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
package com.harmony.umbrella.examples.ee;

import java.util.Properties;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;

import com.harmony.umbrella.context.ee.EJBApplicationContext;

/**
 * @author wuxii@foxmail.com
 */
@WebService
@Stateless
public class TestBean {

	static Properties props = new Properties();

	static {
		props.put("jndi.context.root", " , java:");
		props.put("jndi.context.resolver", "com.harmony.umbrella.context.ee.resolver.wls.WebLogicContextBeanResolver");
	}

	public void test(String beanName) {
		Object bean = EJBApplicationContext.getInstance(props).getBean(beanName);
		System.out.println(bean);
	}

	public void testI(String mappedName) {
		I bean = EJBApplicationContext.getInstance(props).lookup(I.class, mappedName);
		System.out.println(bean.sayHi());
	}

	public void testWls() throws Exception {
		String root = "";
		InitialContext context = new InitialContext();
		Context rootContext = (Context) context.lookup(root);
		NamingEnumeration<NameClassPair> ncps = rootContext.list("");
		while (ncps.hasMoreElements()) {
			NameClassPair ncp = ncps.next();
			String temp = root + "/" + ncp.getName();
			String name = ncp.getName();
			String className = ncp.getClassName();
			String nameInNamespace = null;
			try {
				nameInNamespace = ncp.getNameInNamespace();
			} catch (Exception e1) {
			}
			Object object = null;
			try {
				object = rootContext.lookup(temp);
			} catch (Exception e) {
			}
			System.out.println(">>>>>> name:" + name + ", className:" + className + ", nameInNamespace:" + nameInNamespace + ", hashCode:" + object.hashCode() + ", object:" + object);
			if (object instanceof Context) {
				try {
					ncps = ((Context) object).list("");
					root = temp;
				} catch (Exception e) {
				}
			}
			Thread.sleep(500);
		}
	}

	public void testRoot() throws Exception {
		InitialContext context = new InitialContext();
		Context rootContext = (Context) context.lookup("");
		NamingEnumeration<Binding> bindings = rootContext.listBindings("");
		while (bindings.hasMoreElements()) {
			Binding binding = bindings.next();
			String name = binding.getName();
			String className = binding.getClassName();
			String nameInNamespace = null;
			try {
				nameInNamespace = binding.getNameInNamespace();
			} catch (Exception e) {
			}
			Object object = binding.getObject();
			System.out.println(">>>>>> name:" + name + ", className:" + className + ", nameInNamespace:" + nameInNamespace + ", hashCode:" + object.hashCode() + ", object:" + object);
			if (binding.getObject() instanceof Context) {
				bindings = ((Context) object).listBindings("");
			}
			Thread.sleep(500);
		}
	}

}

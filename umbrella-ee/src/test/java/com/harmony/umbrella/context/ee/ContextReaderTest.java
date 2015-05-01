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

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;

import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.umbrella.context.ee.reader.WeblogicContextReader;

/**
 * @author wuxii@foxmail.com
 */
public class ContextReaderTest {

	private static EJBContainer container;

	@BeforeClass
	public static void setUp() {
		container = EJBContainer.createEJBContainer();
	}

	@Test
	public void test() {
		new ContextReader(container.getContext()).accept(new ContextVisitor() {
			@Override
			public void visitContext(Context context, String jndi) {
			}

			@Override
			public void visitBean(Object bean, String jndi) {
				System.err.println(">> " + jndi);
			}

		}, "java:");
	}

	@Test
	public void testTimeLimitedContextReader() throws Exception {
		long startTime = System.currentTimeMillis();
		new WeblogicContextReader(container.getContext(), 1000 * 10).accept(new ContextVisitor() {
			@Override
			public void visitContext(Context context, String jndi) {
			}

			@Override
			public void visitBean(Object bean, String jndi) {
				System.err.println(">> " + jndi);
			}
		}, "java:");
		System.out.println("use time:" + (System.currentTimeMillis() - startTime));
		// Thread.sleep(Long.MAX_VALUE);
	}

}

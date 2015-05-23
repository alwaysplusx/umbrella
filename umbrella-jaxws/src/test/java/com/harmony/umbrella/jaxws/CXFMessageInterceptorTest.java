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
package com.harmony.umbrella.jaxws;

import java.util.Set;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.junit.Test;

import com.harmony.umbrella.jaxws.JaxWsServerBuilder.JaxWsServerFactoryConfig;
import com.harmony.umbrella.jaxws.services.HelloService;
import com.harmony.umbrella.jaxws.services.HelloServiceImpl;

/**
 * @author wuxii@foxmail.com
 */
public class CXFMessageInterceptorTest {

	private static final String address = "http://localhost:8081/hello";

	@Test
	public void testMessageHandle() {
		JaxWsProxyBuilder builder = JaxWsProxyBuilder.newProxyBuilder();
		builder.getInInterceptors().add(new AbstractPhaseInterceptor<Message>(Phase.RECEIVE) {
			@Override
			public void handleMessage(Message message) throws Fault {
				Set<String> keySet = message.keySet();
				for (String string : keySet) {
					Object obj = message.get(string);
					Class<?> claz = obj != null ? obj.getClass() : null;
					System.out.println(">>>>>>>>> " + string + ", " + obj + ", " + claz);
				}
				System.out.println();
				Set<Class<?>> contentFormats = message.getContentFormats();
				for (Class<?> class1 : contentFormats) {
					Object obj = message.getContent(class1);
					Class<?> claz = obj != null ? obj.getClass() : null;
					System.out.println("******** " + class1 + ", " + obj + ", " + claz);
				}
				System.out.println();
				Set<String> contextualPropertyKeys = message.getContextualPropertyKeys();
				for (String string : contextualPropertyKeys) {
					Object obj = message.getContextualProperty(string);
					Class<?> claz = obj != null ? obj.getClass() : null;
					System.out.println("######## " + string + ", " + obj + ", " + claz);
				}
			}

			@Override
			public void handleFault(Message message) {
			}

		});

		HelloService service = builder.build(HelloService.class, address);

		service.sayHi("wuxii");
	}

	public static void main(String[] args) {
		JaxWsServerBuilder.newServerBuilder().setServiceInterface(HelloService.class).publish(HelloServiceImpl.class, address, new JaxWsServerFactoryConfig() {

			@Override
			public void config(JaxWsServerFactoryBean factoryBean) {
				factoryBean.getInInterceptors().add(new AbstractPhaseInterceptor<Message>(Phase.RECEIVE) {
					@Override
					public void handleMessage(Message message) throws Fault {
						Set<String> keySet = message.keySet();
						for (String string : keySet) {
							Object obj = message.get(string);
							Class<?> claz = obj != null ? obj.getClass() : null;
							System.out.println(">>>>>>>>> " + string + ", " + obj + ", " + claz);
						}
						System.out.println();
						Set<Class<?>> contentFormats = message.getContentFormats();
						for (Class<?> class1 : contentFormats) {
							Object obj = message.getContent(class1);
							Class<?> claz = obj != null ? obj.getClass() : null;
							System.out.println("******** " + class1 + ", " + obj + ", " + claz);
						}
						System.out.println();
						Set<String> contextualPropertyKeys = message.getContextualPropertyKeys();
						for (String string : contextualPropertyKeys) {
							Object obj = message.getContextualProperty(string);
							Class<?> claz = obj != null ? obj.getClass() : null;
							System.out.println("######## " + string + ", " + obj + ", " + claz);
						}
					}

					@Override
					public void handleFault(Message message) {
					}

				});
			}

		});
	}
}

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

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.umbrella.jaxws.services.HelloService;
import com.harmony.umbrella.jaxws.services.HelloServiceImpl;

/**
 * @author wuxii@foxmail.com
 */
public class JaxWsServerAndProxyTest {

	private static final String address = "http://localhost:8080/hi";

	@BeforeClass
	public static void setUp() {
		JaxWsServerBuilder.newServerBuilder().publish(HelloServiceImpl.class, address);
	}

	@Test
	public void testProxyBuilder() {
		HelloService service = JaxWsProxyBuilder.newProxyBuilder().build(HelloService.class, address);
		assertEquals("Hi wuxii", service.sayHi("wuxii"));
	}

	public static void main(String[] args) {
		// 最好是给实现类也添加上与接口一样的annotation配置信息
		JaxWsServerBuilder.newServerBuilder().setServiceInterface(HelloService.class).publish(HelloServiceImpl.class, address);
	}

}

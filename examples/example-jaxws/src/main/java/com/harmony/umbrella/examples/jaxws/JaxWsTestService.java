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
package com.harmony.umbrella.examples.jaxws;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.harmony.umbrella.jaxws.JaxWsProxyBuilder;
import com.harmony.umbrella.jaxws.impl.SimpleJaxWsContext;
import com.harmony.umbrella.jaxws.support.JaxWsExecutorSupport;

/**
 * @author wuxii
 */
@Stateless
@WebService(serviceName = "JaxWsTestService")
public class JaxWsTestService {

	@EJB
	private JaxWsExecutorSupport executorSupport;

	public void testSync(@WebParam(name = "name") String name, @WebParam(name = "address") String address) {
		HelloService service = JaxWsProxyBuilder.newProxyBuilder().setAddress(address).build(HelloService.class);
		String result = service.sayHi("wuxii");
		System.out.println(result);
	}

	public void testAsyn(@WebParam(name = "name") String name, @WebParam(name = "address") String address) {
		SimpleJaxWsContext context = new SimpleJaxWsContext(HelloService.class, "sayHi", new Object[] { "wuxii" });
		context.setAddress(address);
		executorSupport.send(context);
	}

}

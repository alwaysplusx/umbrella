/*
 * Copyright 2002-2015 the original author or authors.
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
package com.harmony.umbrella.ws;

import static org.junit.Assert.*;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.harmony.umbrella.ws.jaxws.JaxWsProxyBuilder;
import com.harmony.umbrella.ws.services.HelloService;
import com.harmony.umbrella.ws.services.HelloWebService;

/**
 * @author wuxii@foxmail.com
 */
public class ServerPublishTest {

    private static final String ADDRESS = "http://localhost:8080/hi";

    public static void main(String[] args) {
        String address = ADDRESS;
        if (args.length > 0) {
            address = args[0];
        }
        Endpoint.publish(address, new HelloWebService());
    }

    @BeforeClass
    public static void beforeClass() {
        ServerManager.getServerManager().publish(HelloWebService.class, ADDRESS);
    }

    @Test
    public void testJAXWS() throws Exception {
        Service service = Service.create(new URL("http://localhost:8080/hi?wsdl"), new QName("http://www.harmony.com/test/hi", "HelloService"));
        HelloService helloService = service.getPort(HelloService.class);
        assertEquals("Hi wuxii", helloService.sayHi("wuxii"));
    }

    @Test
    public void testCXF() {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setAddress(ADDRESS);
        HelloService helloService = factoryBean.create(HelloService.class);
        assertEquals("Hi wuxii", helloService.sayHi("wuxii"));
    }

    @Test
    @Ignore
    public void testCXFPublish() {
        JaxWsServerFactoryBean serverFactoryBean = new JaxWsServerFactoryBean();
        serverFactoryBean.setAddress(ADDRESS);
        serverFactoryBean.setServiceClass(HelloWebService.class);
        // or set service bean
        // serverFactoryBean.setServiceBean(new HelloWebService());
        serverFactoryBean.create();
    }

    @Test
    public void test() {
        HelloService helloService = JaxWsProxyBuilder.create()//
                .setAddress(ADDRESS)//
                .build(HelloService.class);
        assertEquals("Hi wuxii", helloService.sayHi("wuxii"));
    }

}

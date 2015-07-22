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
package com.harmony.umbrella.ws;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.umbrella.ws.jaxws.JaxWsProxyBuilder;
import com.harmony.umbrella.ws.services.HelloRESTService;
import com.harmony.umbrella.ws.services.HelloService;
import com.harmony.umbrella.ws.services.HelloWebService;

/**
 * @author wuxii@foxmail.com
 */
public class ServerManagerTest {

    private static final String address1 = "http://localhost:9001/demo";
    private static final String address2 = "http://localhost:9002/demo";
    private static final ServerManager sm = ServerManager.getServerManager();

    @BeforeClass
    public static void setUp() {
        sm.publish(HelloRESTService.class, address1);
        sm.publish(HelloWebService.class, address2);
    }

    @Test
    public void testREST() throws IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(new HttpGet(address1 + "/hi/wuxii"));
        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("Hi wuxii", EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testWebService() {
        HelloService service = JaxWsProxyBuilder.create().setAddress(address2).build(HelloService.class);
        assertEquals("Hi wuxii", service.sayHi("wuxii"));
    }

    public static void main(String[] args) {
        // http://localhost:8080/rest/hi?_wadl
        sm.publish(HelloRESTService.class, "http://localhost:8080/rest/");
    }

}

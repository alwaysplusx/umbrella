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
package com.harmony.umbrella.ws.jaxws;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.umbrella.ws.ResponseCallback;
import com.harmony.umbrella.ws.cxf.interceptor.MessageInInterceptor;
import com.harmony.umbrella.ws.cxf.interceptor.MessageOutInterceptor;
import com.harmony.umbrella.ws.services.HelloService;
import com.harmony.umbrella.ws.services.HelloWebService;
import com.harmony.umbrella.ws.support.SimpleContext;

/**
 * @author wuxii@foxmail.com
 */
public class JaxWsServerAndProxyTest {

    private static final String address = "http://localhost:8080/hello";
    private static final JaxWsExecutor executor = new JaxWsCXFExecutor();

    private static int count = 0;

    @BeforeClass
    public static void setUp() {
        JaxWsServerBuilder.create()//
                .addInInterceptor(new MessageInInterceptor())//
                .addOutInterceptor(new MessageOutInterceptor())//
                .publish(HelloWebService.class, address);
    }

    @Test
    public void testProxyBuilder() {
        HelloService service = JaxWsProxyBuilder//
                .create()//
                .addInInterceptor(new MessageInInterceptor())
                .addOutInterceptor(new MessageOutInterceptor())
                .build(HelloService.class, address);
        assertEquals("Hi wuxii", service.sayHi("wuxii"));
    }

    @Test
    public void testAsyncAndCallback() {
        SimpleContext context = new SimpleContext(HelloService.class, "sayHi", address, new Object[]{"wuxii"});

        assertEquals(0, count);

        executor.executeAsync(context, new ResponseCallback<String>() {

            @Override
            public void handle(String result, Map<String, Object> content) {
                count++;
                assertEquals("Hi wuxii", result);
            }
        });

        assertEquals(1, count);
    }

    public static void main(String[] args) {
        setUp();
    }

}

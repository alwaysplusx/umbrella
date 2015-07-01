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
package com.harmony.umbrella.ws.jaxws;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.harmony.umbrella.ws.cxf.interceptor.MessageInInterceptor;
import com.harmony.umbrella.ws.cxf.interceptor.MessageOutInterceptor;
import com.harmony.umbrella.ws.services.HelloService;
import com.harmony.umbrella.ws.services.HelloWebService;

/**
 * @author wuxii@foxmail.com
 */
@Ignore
public class JaxWsProxyBuilderBatchTest {

    private static final String address = "http://localhost:8081/hello/batch";

    @BeforeClass
    public static void setUp() {
        // JaxWsServerBuilder.create()//
        // .addInInterceptor(new MessageInInterceptor())//
        // .addOutInterceptor(new MessageOutInterceptor())//
        // .publish(HelloWebService.class, address);
    }

    @Test
    public void test() {

        for (final int index : new int[] { 1, 2, 3, 4, 5 }) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    while (true) {
                        try {
                            HelloService service = JaxWsProxyBuilder.create().build(HelloService.class, address);
                            assertEquals("Hi abc" + index, service.sayHi("abc" + index));
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                        }
                    }
                }

            }).start();
        }

        while (true) {
            try {
                HelloService service = JaxWsProxyBuilder.create().build(HelloService.class, address);
                assertEquals("Hi def", service.sayHi("def"));
                Thread.sleep(400);
            } catch (InterruptedException e) {
            }
        }

    }

    public static void main(String[] args) {
        JaxWsServerBuilder.create()//
                .addInInterceptor(new MessageInInterceptor())//
                .addOutInterceptor(new MessageOutInterceptor())//
                .publish(HelloWebService.class, address);
    }

}

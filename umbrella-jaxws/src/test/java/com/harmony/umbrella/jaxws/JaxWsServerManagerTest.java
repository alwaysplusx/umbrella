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
package com.harmony.umbrella.jaxws;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
public class JaxWsServerManagerTest {

    @Before
    public void setUp() {
        JaxWsServerManager.getInstance().publish(HelloServiceImpl.class, "http://localhost:8080/hi");
    }

    @Test
    public void testPublishClassOfQString() {
        String result = JaxWsProxyBuilder.newProxyBuilder().build(HelloService.class, "http://localhost:8080/hi").sayHi("wuxii");
        assertEquals("Hi wuxii", result);
    }

    @After
    public void tearDown() {
        JaxWsServerManager.getInstance().destoryAll();
    }

}

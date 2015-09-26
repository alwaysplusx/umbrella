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
package com.harmony.umbrella.ee;

import javax.ejb.embeddable.EJBContainer;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
public class EJBBeanFactoryTest {

    public static EJBContainer container;

    private static EJBApplicationContext context;

    @BeforeClass
    public static void beforeClass() throws Exception {
        container = EJBContainer.createEJBContainer();
        context = EJBApplicationContext.getInstance();
    }

    @Test
    public void testContextResolver() throws Exception {
        context.getBean("java:global/umbrella-ee/TestBean");
        context.getBean(TestRemote.class);
    }

}

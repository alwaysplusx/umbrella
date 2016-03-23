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
package com.harmony.umbrella.test;

import static org.junit.Assert.*;

import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
public class SampleTest {

    public static EJBContainer container;

    @EJB
    private SampleRemote sample;

    @BeforeClass
    public static void beforeClass() {
        Properties props = new Properties();
        props.put("openejb.embedded.remotable", "true");
        container = EJBContainer.createEJBContainer(props);
    }

    @Before
    public void setUp() throws Exception {
        container.getContext().bind("inject", this);
    }

    @Test
    public void testSayHi() throws Exception {
        assertEquals("Hi wuxii", sample.sayHi("wuxii"));
        Thread.sleep(Long.MAX_VALUE);
    }

}

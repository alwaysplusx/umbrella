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
package com.harmony.modules.scheduling.support;

import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.harmony.modules.scheduling.Scheduler;

/**
 * @author wuxii@foxmail.com
 */
public class PropertiesFileEJBSchedulerTest {

    private EJBContainer container;
    @EJB(beanName = "PropertiesFileEJBScheduler")
    private Scheduler scheduler;

    @Before
    public void setUp() throws Exception {
        Properties props = new Properties();
        container = EJBContainer.createEJBContainer(props);
        container.getContext().bind("inject", this);
    }

    @After
    public void tearDown() throws Exception {
        container.close();
    }

    @Test
    public void test() throws Exception {
        assertNotNull(scheduler);
        System.err.println(scheduler);
        scheduler.startAll();
        Thread.sleep(1000 * 10);
    }

}

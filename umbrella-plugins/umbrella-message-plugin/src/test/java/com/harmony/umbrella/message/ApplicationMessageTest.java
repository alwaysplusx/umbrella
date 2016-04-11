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
package com.harmony.umbrella.message;

import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.umbrella.config.ConfigurationBeans;
import com.harmony.umbrella.message.jms.JmsMessageSender;

/**
 * @author wuxii@foxmail.com
 */
public class ApplicationMessageTest {

    public static EJBContainer container;

    @EJB
    private JmsMessageSender sender;

    @EJB
    private ConfigurationBeans<MessageResolver> configurationBeans;

    @BeforeClass
    public static void beforeClass() throws Exception {

        Properties props = new Properties();
        props.put("MyJmsResourceAdapter", "new://Resource?type=ActiveMQResourceAdapter");
        props.put("MyJmsResourceAdapter.BrokerXmlConfig", "broker:(tcp://localhost:61616)?useJmx=false");
        props.put("MyJmsResourceAdapter.ServerUrl", "tcp://localhost:61616");

        props.put("MyJmsMdbContainer", "new://Container?type=MESSAGE");
        props.put("MyJmsMdbContainer.ResourceAdapter", "MyJmsResourceAdapter");

        props.put("jms/connectionFactory", "new://Resource?type=QueueConnectionFactory");
        props.put("jms/connectionFactory.ResourceAdapter", "MyJmsResourceAdapter");

        props.put("jms/queue", "new://Resource?type=Queue");

        container = EJBContainer.createEJBContainer(props);

    }

    @Before
    public void setUp() throws Exception {
        container.getContext().bind("inject", this);
    }

    @Test
    public void testMessage() throws Exception {
        sender.send(new ApplicationMessage("Hello World!"));
        Thread.sleep(1000);
    }

    @AfterClass
    public static void afterClass() {
        container.close();
    }

}

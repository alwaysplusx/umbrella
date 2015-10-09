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
package com.harmony.dark.ws;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.dark.ws.ser.UserWebService;
import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.message.MessageResolver;
import com.harmony.umbrella.ws.jaxws.JaxWsProxyBuilder;
import com.harmony.umbrella.ws.jaxws.JaxWsServerManager;

/**
 * @author wuxii@foxmail.com
 */
public class UserProxyTest {

    public static final String address = "http://localhost:8080/user";

    public static EJBContainer container;

    private static ApplicationContext context;

    @EJB
    private UserProxy proxy;

    @BeforeClass
    public static void beforeClass() throws Exception {

        Properties props = new Properties();
        props.put("MyJmsResourceAdapter", "new://Resource?type=ActiveMQResourceAdapter");
        props.put("MyJmsResourceAdapter.BrokerXmlConfig", "broker:(tcp://localhost:61616)?useJmx=false");
        props.put("MyJmsResourceAdapter.ServerUrl", "tcp://localhost:61616");

        props.put("MyJmsMdbContainer", "new://Container?type=MESSAGE");
        props.put("MyJmsMdbContainer.ResourceAdapter", "MyJmsResourceAdapter");

        props.put("jms.jaxws.connectionFactory", "new://Resource?type=QueueConnectionFactory");
        props.put("jms.jaxws.connectionFactory.ResourceAdapter", "MyJmsResourceAdapter");

        props.put("jms.jaxws.queue", "new://Resource?type=Queue");

        container = EJBContainer.createEJBContainer(props);

        context = ApplicationContext.getApplicationContext();

        JaxWsServerManager.getInstance().publish(UserWebService.class, address);

    }

    @Before
    public void before() throws Exception {
        container.getContext().bind("inject", this);
    }

    @Test
    public void testSync() {
        UserService service = JaxWsProxyBuilder.create().build(UserService.class, address);
        service.accessUser("S", new User("wuxii", 18, "F", Calendar.getInstance()));
    }

    @Test
    public void testAsync() throws Exception {
        // FIXME project in umbrella-ee can't lookup other project bean
        context.getBean(UserProxy.class);
        context.getBean(MessageResolver.class);
        proxy.sync(new User("wuxii"));
        Thread.sleep(1000 * 2);
        UserService service = JaxWsProxyBuilder.create().build(UserService.class, address);
        assertNotNull(service.findUser("wuxii"));
    }

    public static void main(String[] args) {
        JaxWsServerManager.getInstance().publish(UserWebService.class, address);
    }

}

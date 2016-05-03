package com.harmony.umbrella.message;

import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.umbrella.config.Configurations;
import com.harmony.umbrella.message.jms.JmsMessageSender;

/**
 * @author wuxii@foxmail.com
 */
public class ApplicationMessageTest {

    public static EJBContainer container;

    @EJB
    private JmsMessageSender sender;

    @EJB
    private Configurations configurations;

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

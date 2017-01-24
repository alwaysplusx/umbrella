package com.harmony.umbrella.message;

import javax.jms.JMSException;

import com.harmony.umbrella.message.activemq.ActiveMQMessageHelperBuilder;

/**
 * @author wuxii@foxmail.com
 */
public class BrokerServiceTest {

    public static void main(String[] args) throws JMSException {
        MessageHelper helper = new ActiveMQMessageHelperBuilder().connectionFactoryURL("tcp://localhost:61616").queueName("test.queue").build();
        // helper.sendTextMessage("Hello World!");
        System.out.println(helper.receiveMessage());
    }

}

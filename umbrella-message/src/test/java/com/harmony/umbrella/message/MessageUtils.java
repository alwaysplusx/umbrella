package com.harmony.umbrella.message;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import com.harmony.umbrella.message.support.SimpleJmsTemplate;

/**
 * @author wuxii@foxmail.com
 */
public class MessageUtils {

    public static void send(String text, ConnectionFactory connectionFactory, Destination destination) throws JMSException {
        SimpleJmsTemplate jmsTemplate = new SimpleJmsTemplate(connectionFactory, destination, true);
        jmsTemplate.start();
        TextMessage message = jmsTemplate.getSession().createTextMessage(text);
        jmsTemplate.getMessageProducer().send(message);
        jmsTemplate.stop();
    }

    public static Message receive(ConnectionFactory connectionFactory, Destination destination) throws JMSException {
        SimpleJmsTemplate jmsTemplate = new SimpleJmsTemplate(connectionFactory, destination, true);
        jmsTemplate.start();
        try {
            return jmsTemplate.getMessageConsumer().receive();
        } finally {
            jmsTemplate.stop();
        }
    }

}

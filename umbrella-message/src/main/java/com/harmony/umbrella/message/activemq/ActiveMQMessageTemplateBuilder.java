package com.harmony.umbrella.message.activemq;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import com.harmony.umbrella.message.MessageTemplateBuilder;

/**
 * @author wuxii@foxmail.com
 */
public class ActiveMQMessageTemplateBuilder extends MessageTemplateBuilder<ActiveMQMessageTemplateBuilder> {

    public static ActiveMQMessageTemplateBuilder createBuilder() {
        return new ActiveMQMessageTemplateBuilder();
    }

    public static ActiveMQMessageTemplateBuilder createBuilder(ConnectionFactory connectionFactory, Destination destination) {
        return new ActiveMQMessageTemplateBuilder().connectionFactory(connectionFactory).destination(destination);
    }

    public static ActiveMQMessageTemplateBuilder createBuilder(String brokerUrl, String destinationName) {
        return new ActiveMQMessageTemplateBuilder().connectionFactoryURL(brokerUrl).destinationName(destinationName);
    }

    public ActiveMQMessageTemplateBuilder connectionFactoryURL(String url) {
        this.connectionFactory = new ActiveMQConnectionFactory(url);
        return this;
    }

    public ActiveMQMessageTemplateBuilder queueName(String queueName) {
        this.destination = new ActiveMQQueue(queueName);
        return this;
    }

    public ActiveMQMessageTemplateBuilder topicName(String topicName) {
        this.destination = new ActiveMQTopic(topicName);
        return this;
    }

    public ActiveMQMessageTemplateBuilder destinationName(String destinationName) {
        final Destination dest;
        if (destinationName.startsWith("topic://") && destinationName.length() > 8) {
            dest = new ActiveMQTopic(destinationName.substring(8));
        } else if (destinationName.startsWith("queue://") && destinationName.length() > 8) {
            dest = new ActiveMQQueue(destinationName.substring(8));
        } else {
            dest = new ActiveMQQueue(destinationName);
        }
        this.destination = dest;
        return this;
    }

}
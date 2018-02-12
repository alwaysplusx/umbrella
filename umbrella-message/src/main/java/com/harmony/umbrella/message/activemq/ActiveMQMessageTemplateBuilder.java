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

    public static ActiveMQMessageTemplateBuilder newBuilder() {
        return new ActiveMQMessageTemplateBuilder();
    }

    public static ActiveMQMessageTemplateBuilder newBuilder(ConnectionFactory connectionFactory, Destination destination) {
        return new ActiveMQMessageTemplateBuilder().setConnectionFactory(connectionFactory).setDestination(destination);
    }

    public static ActiveMQMessageTemplateBuilder newBuilder(String brokerUrl, String destinationName) {
        return new ActiveMQMessageTemplateBuilder().setBrokerUrl(brokerUrl).setDestinationName(destinationName);
    }

    public ActiveMQMessageTemplateBuilder setBrokerUrl(String url) {
        return setConnectionFactory(new ActiveMQConnectionFactory(url));
    }

    public ActiveMQMessageTemplateBuilder setQueueName(String queueName) {
        return setDestination(new ActiveMQQueue(queueName));
    }

    public ActiveMQMessageTemplateBuilder setTopicName(String topicName) {
        return setDestination(new ActiveMQTopic(topicName));
    }

    public ActiveMQMessageTemplateBuilder setDestinationName(String destinationName) {
        final Destination dest;
        if (destinationName.length() > 8 && destinationName.startsWith("topic://")) {
            dest = new ActiveMQTopic(destinationName.substring(8));
        } else if (destinationName.length() > 8 && destinationName.startsWith("queue://")) {
            dest = new ActiveMQQueue(destinationName.substring(8));
        } else {
            dest = new ActiveMQQueue(destinationName);
        }
        return setDestination(dest);
    }

}
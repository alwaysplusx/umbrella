package com.harmony.umbrella.message.activemq;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import com.harmony.umbrella.message.MessageHelperBuilder;

/**
 * @author wuxii@foxmail.com
 */
public class ActiveMQMessageHelperBuilder extends MessageHelperBuilder<ActiveMQMessageHelperBuilder> {

    public static ActiveMQMessageHelperBuilder createBuilder() {
        return new ActiveMQMessageHelperBuilder();
    }

    public static ActiveMQMessageHelperBuilder createBuilder(ConnectionFactory connectionFactory, Destination destination) {
        return new ActiveMQMessageHelperBuilder().connectionFactory(connectionFactory).destination(destination);
    }

    public static ActiveMQMessageHelperBuilder createBuilder(String brokerUrl, String destinationName) {
        return new ActiveMQMessageHelperBuilder().connectionFactoryURL(brokerUrl).destinationName(destinationName);
    }

    public ActiveMQMessageHelperBuilder connectionFactoryURL(String url) {
        this.connectionFactory = new ActiveMQConnectionFactory(url);
        return this;
    }

    public ActiveMQMessageHelperBuilder queueName(String queueName) {
        this.destination = new ActiveMQQueue(queueName);
        return this;
    }

    public ActiveMQMessageHelperBuilder topicName(String topicName) {
        this.destination = new ActiveMQTopic(topicName);
        return this;
    }

    public ActiveMQMessageHelperBuilder destinationName(String destinationName) {
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
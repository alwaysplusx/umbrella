package com.harmony.umbrella.message;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Topic;

/**
 * @author wuxii@foxmail.com
 */
public class JmsResources {

    private ConnectionFactory connectionFactory;
    private Destination destination;

    public JmsResources(ConnectionFactory connectionFactory, Destination destination) {
        if (connectionFactory == null) {
            throw new IllegalArgumentException("connection factory must not null");
        }
        if (destination == null) {
            throw new IllegalArgumentException("destination must not null");
        }
        this.connectionFactory = connectionFactory;
        this.destination = destination;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public Destination getDestination() {
        return destination;
    }

    public String getDestinationName() {
        try {
            if (destination instanceof Queue) {
                return ((Queue) destination).getQueueName();
            } else if (destination instanceof Topic) {
                return ((Topic) destination).getTopicName();
            }
        } catch (JMSException e) {
            // exception is null
        }
        return null;
    }

    public boolean isQueue() {
        return destination instanceof Queue;
    }

    public boolean isTopic() {
        return destination instanceof Topic;
    }

}

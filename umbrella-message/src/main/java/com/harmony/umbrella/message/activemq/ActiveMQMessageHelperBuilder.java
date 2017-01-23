package com.harmony.umbrella.message.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import com.harmony.umbrella.message.MessageHelper.MessageHelperBuilder;

/**
 * @author wuxii@foxmail.com
 */
public class ActiveMQMessageHelperBuilder extends MessageHelperBuilder<ActiveMQMessageHelperBuilder> {

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

}
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
package com.harmony.umbrella.message.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleJmsConfig implements JmsConfig {

    private ConnectionFactory connectionFactory;
    private Destination destination;

    private Connection connection;
    private Session session;
    private MessageProducer messageProducer;
    private MessageConsumer messageConsumer;

    public SimpleJmsConfig(ConnectionFactory connectionFactory, Destination destination) {
        this.connectionFactory = connectionFactory;
        this.destination = destination;
    }

    public void start() throws JMSException {
        // stop/clear first
        stop();
        this.connection = connectionFactory.createConnection();
        this.connection.start();
    }

    public Connection getConnection() throws JMSException {
        return connection;
    }

    public Session getSession() throws JMSException {
        if (session == null) {
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
        }
        return session;
    }

    public MessageProducer getMessageProducer() throws JMSException {
        if (messageProducer == null) {
            messageProducer = getSession().createProducer(destination);
        }
        return messageProducer;
    }

    public MessageConsumer getMessageConsumer() throws JMSException {
        if (messageConsumer == null) {
            messageConsumer = getSession().createConsumer(destination);
        }
        return messageConsumer;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public Destination getDestination() {
        return destination;
    }

    public void stop() throws JMSException {
        if (messageConsumer != null) {
            try {
                messageConsumer.close();
            } catch (JMSException e) {
            }
            messageConsumer = null;
        }
        if (messageProducer != null) {
            try {
                messageProducer.close();
            } catch (JMSException e) {
            }
            messageProducer = null;
        }
        if (session != null) {
            try {
                session.close();
            } catch (JMSException e) {
            }
            session = null;
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
            }
            connection = null;
        }
    }

}

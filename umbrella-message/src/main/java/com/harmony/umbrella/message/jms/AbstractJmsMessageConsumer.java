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
import javax.jms.Session;

import com.harmony.umbrella.message.Message;
import com.harmony.umbrella.message.MessageException;
import com.harmony.umbrella.message.MessageResolverChain;

/**
 * @author wuxii@foxmail.com
 */
public class AbstractJmsMessageConsumer implements JmsMessageConsumer {

    protected javax.jms.Message consomeJMS(JmsConsomeConfig config) throws JMSException, MessageException {
        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        try {
            connection = getConnectionFactory().createConnection();
            connection.start();
            if (config != null) {
                session = connection.createSession(config.transacted(), config.sessionMode());
            } else {
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            }
            consumer = session.createConsumer(getDestination());
            if (config != null) {
                config.configMessageConsumer(consumer);
            }
            return consumer.receive(DEFAULT_RECEIVE_TIMEOUT);
        } finally {
            if (consumer != null) {
                consumer.close();
            }
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public Message consome(JmsConsomeConfig config) throws MessageException {
        try {
            javax.jms.Message jmsMessage = consomeJMS(config);
            if (jmsMessage != null) {

            }
        } catch (JMSException e) {
        }
        return null;
    }

    @Override
    public void consume(MessageResolverChain chain, JmsConsomeConfig config) {
    }

    @Override
    public Message consome() throws MessageException {
        return consome(null);
    }

    @Override
    public void consume(MessageResolverChain chain) {
    }

    /**
     * JMS连接工厂
     * 
     * @return
     */
    protected ConnectionFactory getConnectionFactory() {
        return null;
    }

    /**
     * JMS的目的地
     * 
     * @return
     */
    protected Destination getDestination() {
        return null;
    }

}

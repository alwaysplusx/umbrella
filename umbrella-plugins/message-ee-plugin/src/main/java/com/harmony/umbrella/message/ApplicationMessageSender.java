/*
 * Copyright 2002-2015 the original author or authors.
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
package com.harmony.umbrella.message;

import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import com.harmony.umbrella.message.MessageSender;
import com.harmony.umbrella.message.ee.AbstractJmsMessageSender;

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = "ApplicationMessageSender")
@Remote({ MessageSender.class })
public class ApplicationMessageSender extends AbstractJmsMessageSender implements MessageSender {

    @Resource
    private ConnectionFactory connectionFactory;

    @Resource
    private Destination destination;

    @Override
    public boolean send(Message message) {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            connection = getConnectionFactory().createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            log.info("message listener -> {}", session.getMessageListener());
            producer = session.createProducer(getDestination());
            ObjectMessage om = session.createObjectMessage();
            om.setObject(message);
            producer.send(om);
        } catch (JMSException e) {
            log.error("发送失败{}", message, e);
            return false;
        } finally {
            try {
                if (producer != null) {
                    producer.close();
                }
                if (session != null) {
                    session.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (JMSException e) {
                log.debug("", e);
            }
        }
        return true;
    }

    @Override
    public ConnectionFactory getConnectionFactory() {
        return this.connectionFactory;
    }

    @Override
    public Destination getDestination() {
        return this.destination;
    }

}

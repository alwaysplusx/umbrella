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
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

import com.harmony.umbrella.message.Message;
import com.harmony.umbrella.message.MessageSender;

/**
 * 基于JMS的消息发送基础抽象类
 * 
 * @author wuxii@foxmail.com
 */
public abstract class AbstractJmsMessageSender implements MessageSender {

    private static final Log log = Logs.getLog(AbstractJmsMessageSender.class);

    @Override
    public boolean send(Message message) {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            connection = getConnectionFactory().createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
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

    /**
     * JMS连接工厂
     * 
     * @return
     */
    protected abstract ConnectionFactory getConnectionFactory();

    /**
     * JMS的目的地
     * 
     * @return
     */
    protected abstract Destination getDestination();

}

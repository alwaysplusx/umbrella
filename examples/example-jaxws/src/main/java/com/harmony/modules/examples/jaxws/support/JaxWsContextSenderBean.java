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
package com.harmony.modules.examples.jaxws.support;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import com.harmony.modules.examples.jaxws.JaxWsMessage;
import com.harmony.modules.jaxws.JaxWsContext;
import com.harmony.modules.jaxws.support.JaxWsContextSender;

/**
 * @author wuxii
 */
@Stateless
public class JaxWsContextSenderBean implements JaxWsContextSender {

    @Resource(name = "jms/connectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(name = "jms/queue")
    private Destination destination;

    @Override
    public boolean send(JaxWsContext context) {

        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
            ObjectMessage om = session.createObjectMessage();
            om.setObject(new JaxWsMessage(context));
            producer.send(om);
        } catch (JMSException e) {
            // log
            return false;
        } finally {
            try {
                if (producer != null)
                    producer.close();
                if (session != null)
                    session.close();
                if (connection != null)
                    connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public void open() {
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public boolean isClosed() {
        return false;
    }

}

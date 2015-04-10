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
package com.harmony.umbrella.examples.jaxws.support;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import com.harmony.umbrella.examples.jaxws.JaxWsMessage;
import com.harmony.umbrella.jaxws.JaxWsContext;
import com.harmony.umbrella.jaxws.support.AbstractJmsContextSender;
import com.harmony.umbrella.jaxws.support.JaxWsContextSender;
import com.harmony.umbrella.message.Message;

/**
 * @author wuxii
 */
@Stateless
public class JaxWsContextSenderBean extends AbstractJmsContextSender implements JaxWsContextSender {

    @Resource(name = "jms/connectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(name = "jms/queue")
    private Destination destination;

    @Override
    public Message createMessage(JaxWsContext context) {
        return new JaxWsMessage(context);
    }

    @Override
    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    @Override
    public Destination getDestination() {
        return destination;
    }

}

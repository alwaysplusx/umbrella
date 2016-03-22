/*
 * Copyright 2012-2016 the original author or authors.
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
import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import com.harmony.umbrella.message.jms.AbstractJmsMessageSender;
import com.harmony.umbrella.message.jms.JmsMessageSender;
import com.harmony.umbrella.message.jms.JmsConfig;

/**
 * @author wuxii@foxmail.com
 */
@Remote({ MessageSender.class, JmsMessageSender.class })
@Stateless(mappedName = JmsMessageSender.DEFAULT_MESSAGE_SENDER_MAPPEDNAME)
public class ApplicationMessageSender extends AbstractJmsMessageSender {

    @Resource(name = JmsConfig.DEFAULT_CONNECTION_FACTORY)
    private ConnectionFactory connectionFactory;
    @Resource(name = JmsConfig.DEFAULT_DESTINATION)
    private Destination destination;

    @Override
    protected ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    @Override
    protected Destination getDestination() {
        return destination;
    }

}

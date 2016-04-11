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

import static com.harmony.umbrella.message.ApplicationMessageConstants.*;

import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import com.harmony.umbrella.message.jms.AbstractJmsMessageConsumer;
import com.harmony.umbrella.message.jms.JmsMessageConsumer;

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = "ApplicationMessageConsumer")
@Remote({ MessageConsumer.class, JmsMessageConsumer.class })
public class ApplicationMessageConsumer extends AbstractJmsMessageConsumer {

    @Resource(name = CONNECTION_FACTORY_NAME)
    private ConnectionFactory connectionFactory;
    @Resource(name = QUEUE_NAME)
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

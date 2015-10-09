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
package com.harmony.umbrella.ws;

import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import com.harmony.umbrella.message.jms.AbstractJmsMessageSender;
import com.harmony.umbrella.ws.Context;
import com.harmony.umbrella.ws.support.ContextMessage;
import com.harmony.umbrella.ws.support.ContextSender;

/**
 * JaxWs Context 消息发送工具类
 * 
 * @author wuxii@foxmail.com
 */
@Remote(ContextSender.class)
@Stateless(mappedName = "ContextSenderBean")
public class ContextSenderBean extends AbstractJmsMessageSender implements ContextSender {

    @Resource(name = "jms.jaxws.connectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(name = "jms.jaxws.queue")
    private Destination destination;

    @Override
    public void open() {
    }

    @Override
    public boolean send(Context context) {
        return super.send(new ContextMessage(context));
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public boolean isClosed() {
        return false;
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

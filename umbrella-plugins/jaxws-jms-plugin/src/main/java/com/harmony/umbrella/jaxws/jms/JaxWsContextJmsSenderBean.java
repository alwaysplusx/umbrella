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
package com.harmony.umbrella.jaxws.jms;

import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import com.harmony.umbrella.jaxws.JaxWsContext;
import com.harmony.umbrella.jaxws.support.JaxWsContextSender;
import com.harmony.umbrella.message.Message;

/**
 * {@linkplain JaxWsMessage}消息的生产者, 在通过JMS方式发送出去
 * <p>
 * 中间的消息扭转依赖于{@linkplain com.harmony.umbrella.message.MessageListener
 * MessageListener}. 将生产的消息给特定的消费者消费
 * <p>
 * 主要用于JavaEE环境中
 * 
 * @author wuxii
 */
@Remote(JaxWsContextSender.class)
@Stateless(mappedName = "JaxWsContextJmsSenderBean")
public class JaxWsContextJmsSenderBean extends JaxWsContextJmsSenderAdapter {

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

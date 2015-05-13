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

import com.harmony.umbrella.jaxws.JaxWsContext;
import com.harmony.umbrella.jaxws.support.JaxWsContextReceiver;
import com.harmony.umbrella.jaxws.support.JaxWsContextSender;
import com.harmony.umbrella.jaxws.support.PropertiesFileJaxWsContextReceiver;
import com.harmony.umbrella.message.AbstractMessageResolver;
import com.harmony.umbrella.message.Message;

/**
 * 消费由{@linkplain JaxWsContextSender}产生的{@linkplain JaxWsMessage}消息
 * <p>
 * 中间的消息扭转依赖于{@linkplain com.harmony.umbrella.message.MessageListener
 * MessageListener}. 将监听到的消息给当前的bean消费
 * <p>
 * 该部分主要用与JavaEE环境中
 * 
 * @author wuxii
 */
public class JaxWsContextJmsReceiverBean extends AbstractMessageResolver<JaxWsContext> implements JaxWsContextReceiver {

    private JaxWsContextReceiver receiver = new PropertiesFileJaxWsContextReceiver();

    @Override
    public boolean support(Message message) {
        return message instanceof JaxWsMessage;
    }

    @Override
    public void process(JaxWsContext message) {
        receive(message);
    }

    @Override
    protected JaxWsContext resolver(Message message) {
        return ((JaxWsMessage) message).getMessage();
    }

    @Override
    public void receive(JaxWsContext context) {
        receiver.receive(context);
    }

    @Override
    public void open() throws Exception {
        receiver.open();
    }

    @Override
    public void close() throws Exception {
        receiver.close();
    }

    @Override
    public boolean isClosed() {
        return receiver.isClosed();
    }

    public void setJaxWsContextReceiver(JaxWsContextReceiver receiver) {
        this.receiver = receiver;
    }
}

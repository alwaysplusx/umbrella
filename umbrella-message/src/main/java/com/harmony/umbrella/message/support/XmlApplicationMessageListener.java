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
package com.harmony.umbrella.message.support;

import java.io.Serializable;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import com.harmony.umbrella.message.AbstractMessageListener;
import com.harmony.umbrella.message.MessageResolver;

/**
 * for spring
 * 
 * @author wuxii@foxmail.com
 */
public class XmlApplicationMessageListener extends AbstractMessageListener implements MessageListener {

    @Override
    public void init() {
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            try {
                Serializable object = ((ObjectMessage) message).getObject();
                if (object instanceof com.harmony.umbrella.message.Message) {
                    onMessage((com.harmony.umbrella.message.Message) object);
                    return;
                }
                LOG.warn("接受的消息{}不能转化为目标类型[{}], 忽略该消息", message, com.harmony.umbrella.message.Message.class);
            } catch (JMSException e) {
                LOG.error("", e);
            }
        }
    }

    @Override
    public void destroy() {
    }

    public List<MessageResolver> getMessageResolvers() {
        return this.messageResolvers;
    }

    public void setMessageResolvers(List<MessageResolver> messageResolvers) {
        this.messageResolvers = messageResolvers;
    }

}

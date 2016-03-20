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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.Remote;
import javax.jms.MessageListener;

import com.harmony.umbrella.context.ee.ConfigurationBeans;
import com.harmony.umbrella.message.MessageResolver;
import com.harmony.umbrella.message.jms.AbstractJmsMessageListener;

/**
 * JaxWs Context 消息监听类
 * 
 * @author wuxii@foxmail.com
 */
@MessageDriven(activationConfig = { 
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "jms.jaxws.queue") 
})
@Remote({ com.harmony.umbrella.message.MessageListener.class })
public class ContextMessageListener extends AbstractJmsMessageListener implements MessageListener {

    @EJB(mappedName = "MessageResolverBeanConfiguration")
    private ConfigurationBeans<MessageResolver> configuration;

    @Override
    @PostConstruct
    public void init() {
    }

    @Override
    protected List<MessageResolver> getMessageResolvers() {
        return configuration.getBeans();
    }

    @Override
    @PreDestroy
    public void destroy() {
    }

}

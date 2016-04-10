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

import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.Remote;
import javax.jms.MessageListener;

import com.harmony.umbrella.config.ConfigurationBeans;
import com.harmony.umbrella.message.jms.AbstractJmsMessageListener;
import com.harmony.umbrella.message.jms.JmsConfig;


/**
 * @author wuxii@foxmail.com
 */
@MessageDriven(mappedName = JmsConfig.DEFAULT_QUEUE, activationConfig = { 
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = JmsConfig.DEFAULT_QUEUE) 
})
@Remote({ MessageListener.class, com.harmony.umbrella.message.MessageListener.class })
public class ApplicationMessageListener extends AbstractJmsMessageListener implements MessageListener {

    public static final String ApplicationMessageListenerInjectMappedName = "ApplicationMessageListenerConfigurationBeans";

    @EJB(mappedName = ApplicationMessageListenerInjectMappedName)
    private ConfigurationBeans<MessageResolver> configuration;

    @EJB
    private MessageResolver resolver;
    
    @Override
    public void init() {
    }

    @Override
    protected List<MessageResolver> getMessageResolvers() {
        return configuration.getBeans();
    }

    @Override
    public void destroy() {
    }

}

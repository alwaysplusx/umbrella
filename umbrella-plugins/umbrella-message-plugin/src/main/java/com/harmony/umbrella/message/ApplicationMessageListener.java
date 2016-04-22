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

import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.Remote;
import javax.jms.MessageListener;

import com.harmony.umbrella.config.Configurations;
import com.harmony.umbrella.message.jms.AbstractJmsMessageListener;

/**
 * @author wuxii@foxmail.com
 */
@MessageDriven(mappedName = QUEUE_NAME, activationConfig = { 
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
@Remote({ MessageListener.class, com.harmony.umbrella.message.MessageListener.class })
public class ApplicationMessageListener extends AbstractJmsMessageListener implements MessageListener {

    @EJB(mappedName = Configurations.APPLICATION_CONFIGURATIONS)
    private Configurations configurations;

    public static final String ApplicationMessageResolver = "applicationMessageResolver";

    @EJB
    private MessageResolver resolver;

    @Override
    public void init() {
    }

    @Override
    protected List<MessageResolver> getMessageResolvers() {
        return configurations.getBeans(ApplicationMessageResolver);
    }

    @Override
    public void destroy() {
    }

}

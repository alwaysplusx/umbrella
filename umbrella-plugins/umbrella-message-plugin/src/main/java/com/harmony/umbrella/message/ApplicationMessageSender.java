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

import static com.harmony.umbrella.config.Configurations.*;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import com.harmony.umbrella.config.Configurations;
import com.harmony.umbrella.message.jms.AbstractJmsMessageSender;
import com.harmony.umbrella.message.jms.JmsMessageSender;

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = "ApplicationMessageSender")
@Remote({ MessageSender.class, JmsMessageSender.class })
public class ApplicationMessageSender extends AbstractJmsMessageSender {

    public static final String ApplicationDestination = "applicationDestination";
    public static final String ApplicationConnectionFactory = "applicationConnectionFactory";

    @EJB(mappedName = APPLICATION_CONFIGURATIONS, beanName = APPLICATION_CONFIGURATIONS)
    private Configurations config;

    @Override
    protected ConnectionFactory getConnectionFactory() {
        return config.getBean(ApplicationConnectionFactory);
    }

    @Override
    protected Destination getDestination() {
        return config.getBean(ApplicationDestination);
    }

}

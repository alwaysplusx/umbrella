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
package com.harmony.umbrella.message;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.Constants;
import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.SimpleBeanFactory;
import com.harmony.umbrella.message.AbstractMessageListener;
import com.harmony.umbrella.message.MessageResolver;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.PropUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * 系统消息JMS监听.初始加载指定包{@linkplain ApplicationMessageListener#basePackage
 * basePackage}下的所有{@linkplain MessageResolver} 作为消息的处理.
 * 
 * @author wuxii@foxmail.com
 */
@MessageDriven(activationConfig = { 
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "jms.queue")
})
public class ApplicationMessageListener extends AbstractMessageListener implements javax.jms.MessageListener {

    private static final Logger log = LoggerFactory.getLogger(ApplicationMessageListener.class);

    private final Properties props = new Properties();

    private BeanFactory beanFactory = new SimpleBeanFactory();

    public ApplicationMessageListener() {
        this(Constants.GLOBAL_CONFIG);
    }

    public ApplicationMessageListener(String configLocation) {
        try {
            this.props.putAll(PropUtils.filterStartWith("application.message", PropUtils.loadProperties(configLocation)));
        } catch (IOException e) {
            log.warn("can't open [{}] because not exists", configLocation);
        }
    }

    @Override
    @PostConstruct
    public void init() {
        String property = props.getProperty("application.message.resolver");
        if (StringUtils.isNotBlank(property)) {
            String[] resolverNames = property.split(",");
            for (String resolverName : resolverNames) {
                try {
                    Class<?> clazz = ClassUtils.forName(resolverName.trim(), null);
                    if (MessageResolver.class.isAssignableFrom(clazz)) {
                        this.addMessageResolver((MessageResolver) beanFactory.getBean(clazz));
                    }
                } catch (Throwable e) {
                    log.error("class {} is not message resolver class", e);
                }
            }
        }
    }

    /**
     * 为JMS提供. 只处理消息类型为{@linkplain com.harmony.umbrella.message.Message}的消息.
     * 如果不为该类型的消息则忽略
     * 
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    @Override
    public void onMessage(javax.jms.Message message) {
        log.debug("application message listener on message, current message is {}", message);
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
    @PreDestroy
    public void destroy() {
    }

}
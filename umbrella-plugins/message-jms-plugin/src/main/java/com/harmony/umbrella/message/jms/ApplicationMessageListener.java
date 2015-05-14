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
package com.harmony.umbrella.message.jms;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Modifier;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.NoSuchBeanFindException;
import com.harmony.umbrella.core.ClassFilter;
import com.harmony.umbrella.io.util.ResourceScaner;
import com.harmony.umbrella.message.AbstractMessageListener;
import com.harmony.umbrella.message.MessageResolver;

/**
 * 系统消息JMS监听.初始加载指定包{@linkplain ApplicationMessageListener#basePackage
 * basePackage}下的所有{@linkplain MessageResolver} 作为消息的处理.
 * 
 * @author wuxii
 */
@MessageDriven(mappedName = "jms/queue", 
    activationConfig = { 
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") 
    }
)
public class ApplicationMessageListener extends AbstractMessageListener implements javax.jms.MessageListener {

    private static final String basePackage = "com.harmony";
    private BeanFactory beanFactory = ApplicationContext.getApplicationContext();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @PostConstruct
    public void init() {
        super.init();
        try {
            Class<?>[] classes = ResourceScaner.getInstance().scanPackage(basePackage, new ClassFilter() {
                @Override
                public boolean accept(Class<?> clazz) {
                    if (clazz.isInterface())
                        return false;
                    if (Modifier.isAbstract(clazz.getModifiers()))
                        return false;
                    if (!Modifier.isPublic(clazz.getModifiers()))
                        return false;
                    if (!MessageResolver.class.isAssignableFrom(clazz))
                        return false;
                    return true;
                }
            });
            for (Class clazz : classes) {
                try {
                    MessageResolver resolver = (MessageResolver) beanFactory.getBean(clazz);
                    this.addMessageResolver(resolver);
                } catch (NoSuchBeanFindException e) {
                    log.error("{} can't resolver", clazz, e);
                }
            }
            log.info("application message listener init success, with {}", messageResolvers);
        } catch (IOException e) {
            log.error("", e);
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
        if (message instanceof ObjectMessage) {
            try {
                Serializable object = ((ObjectMessage) message).getObject();
                if (object instanceof com.harmony.umbrella.message.Message) {
                    onMessage((com.harmony.umbrella.message.Message) object);
                    return;
                }
                log.warn("接受的消息{}不能转化为目标类型[{}], 忽略该消息", message, com.harmony.umbrella.message.Message.class);
            } catch (JMSException e) {
                log.error("", e);
            }
        }
    }
    
    @Override
    @PreDestroy
    public void destory() {
        super.destory();
    }
    
}

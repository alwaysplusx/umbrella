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
package com.harmony.modules.examples.jaxws;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Modifier;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import com.harmony.modules.bean.BeanLoader;
import com.harmony.modules.bean.ClassBeanLoader;
import com.harmony.modules.io.utils.ResourceScaner;
import com.harmony.modules.message.AbstractMessageListener;
import com.harmony.modules.message.MessageResolver;
import com.harmony.modules.utils.ClassFilter;

/**
 * @author wuxii
 */
@MessageDriven(mappedName = "jms/queue", 
    activationConfig = { 
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") 
})
public class ApplicationMessageListener extends AbstractMessageListener implements javax.jms.MessageListener {

    private static final String basePackage = "com.harmony";
    private BeanLoader beanLoader = new ClassBeanLoader();

    @PostConstruct
    public void postConstruct() {
        init();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void init() {
        super.init();
        try {
            Class<?>[] classes = ResourceScaner.getInstance().scanPackage(basePackage, new ClassFilter() {
                @Override
                public boolean accept(Class<?> clazz) {
                    if (clazz.isInterface())
                        return false;
                    if (clazz.getModifiers() != Modifier.PUBLIC)
                        return false;
                    if (!MessageResolver.class.isAssignableFrom(clazz))
                        return false;
                    return true;
                }
            });
            for (Class clazz : classes) {
                MessageResolver resolver = (MessageResolver) beanLoader.loadBean(clazz);
                addMessageResolver(resolver);
            }
        } catch (IOException e) {
            log.error("", e);
        }
    }

    /*
     * 为JMS提供
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    @Override
    public void onMessage(javax.jms.Message message) {
        if (message instanceof ObjectMessage) {
            try {
                Serializable object = ((ObjectMessage) message).getObject();
                if (object instanceof com.harmony.modules.message.Message) {
                    onMessage((com.harmony.modules.message.Message) object);
                    return;
                }
                log.warn("接受的消息{}不能转化为目标类型[{}], 忽略该消息", message, com.harmony.modules.message.Message.class);
            } catch (JMSException e) {
                log.error("", e);
            }
        }
    }

    @PreDestroy
    public void preDestroy() {
        destory();
    }
}

package com.harmony.umbrella.message;

import org.springframework.jms.listener.SimpleMessageListenerContainer;

/**
 * @author wuxii@foxmail.com
 */
public class MessageListenerBuilder extends SimpleMessageListenerContainer {

    public static void main(String[] args) {
        // MessageTemplate messageTemplate = null;
        // JmsTemplate jmsTemplate = messageTemplate.getJmsTemplate();
        // SimpleMessageListenerContainer listener = new SimpleMessageListenerContainer();
        // listener.setConnectionFactory(jmsTemplate.getConnectionFactory());
        // listener.setDestination(jmsTemplate.getDestination());
        // listener.setAutoStartup(true);
        // listener.setMessageSelector(jmsTemplate.getMessageSelector());
    }

}

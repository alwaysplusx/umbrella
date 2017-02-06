package com.harmony.umbrella.message.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import com.harmony.umbrella.message.DynamicMessageListener;
import com.harmony.umbrella.message.JmsTemplate;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleDynamicMessageListener implements DynamicMessageListener {

    private MessageListener messageListener;

    private JmsTemplate jmsTemplate;

    public SimpleDynamicMessageListener() {
    }

    public SimpleDynamicMessageListener(JmsTemplate jmsTemplate, MessageListener messageListener) {
        this.messageListener = messageListener;
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void start() throws JMSException {
        this.stop();
        this.jmsTemplate.start();
        this.jmsTemplate.getMessageConsumer().setMessageListener(this);
    }

    @Override
    public void onMessage(Message message) {
        try {
            this.messageListener.onMessage(message);
            this.jmsTemplate.commit();
        } catch (Exception e) {
            try {
                this.jmsTemplate.rollback();
            } catch (JMSException e1) {
                throw new IllegalStateException("rollback failed!", e);
            }
            throw new IllegalStateException("message consume failed!", e);
        }
    }

    @Override
    public void stop() throws JMSException {
        if (this.jmsTemplate != null) {
            this.jmsTemplate.stop();
        }
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        if (jmsTemplate.isStarted()) {
            throw new IllegalStateException("outer jmsTemplate is already started");
        }
        if (this.jmsTemplate != null && this.jmsTemplate.isStarted()) {
            throw new IllegalStateException("inner jmsTemplate is already started");
        }
        this.jmsTemplate = jmsTemplate;
    }

    public void setMessageListener(MessageListener messageListener) {
        if (jmsTemplate.isStarted()) {
            throw new IllegalArgumentException("listener is started, can't set message listener");
        }
        this.messageListener = messageListener;
    }

    public MessageListener getMessageListener() {
        return messageListener == null ? this : messageListener;
    }

}

package com.harmony.umbrella.message.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import com.harmony.umbrella.message.DynamicMessageListener;
import com.harmony.umbrella.message.JmsTemplate;
import com.harmony.umbrella.message.MessageAbortException;

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
        Exception exception = null;
        try {
            this.messageListener.onMessage(message);
        } catch (Exception e) {
            exception = e;
        }
        if (exception == null || exception instanceof MessageAbortException) {
            try {
                this.jmsTemplate.commit();
            } catch (JMSException e) {
                if (exception != null) {
                    exception.addSuppressed(e);
                }
                throw new IllegalStateException("commit failure", exception);
            }
        } else {
            try {
                this.jmsTemplate.rollback();
            } catch (JMSException e) {
                exception.addSuppressed(e);
                throw new IllegalStateException("rollback failure", exception);
            }
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

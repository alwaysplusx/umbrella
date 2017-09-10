package com.harmony.umbrella.message.listener;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import com.harmony.umbrella.message.MessageEventListener;
import com.harmony.umbrella.message.MessageEventListener.DestinationType;
import com.harmony.umbrella.message.MessageEventListener.EventPhase;
import com.harmony.umbrella.message.MessageEventListener.MessageEvent;

/**
 * @author wuxii@foxmail.com
 */
public class PhaseMessageListener implements MessageListener {

    private MessageListener messageListener;
    private MessageEventListener messageEventListener;

    public PhaseMessageListener() {
    }

    public PhaseMessageListener(MessageListener messageListener, MessageEventListener messageEventListener) {
        this.messageListener = messageListener;
        this.messageEventListener = messageEventListener;
    }

    @Override
    public void onMessage(Message message) {
        try {
            this.fireEvent(message, EventPhase.BEFORE_CONSUME);
            messageListener.onMessage(message);
            this.fireEvent(message, EventPhase.AFTER_CONSUME);
        } catch (Exception e) {
            this.fireEvent(message, EventPhase.CONSUME_FAILURE);
            throw e;
        }
    }

    protected void fireEvent(final Message message, EventPhase eventPhase) {
        messageEventListener.onEvent(new MessageEvent() {

            @Override
            public Message getMessage() {
                return message;
            }

            @Override
            public EventPhase getEventPhase() {
                return eventPhase;
            }

            @Override
            public DestinationType getDestinationType() {
                Destination destination;
                try {
                    destination = message.getJMSDestination();
                    return DestinationType.forType(destination.getClass());
                } catch (JMSException e) {
                    return null;
                }
            }
        });
    }

    public MessageListener getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public MessageEventListener getMessageEventListener() {
        return messageEventListener;
    }

    public void setMessageEventListener(MessageEventListener messageEventListener) {
        this.messageEventListener = messageEventListener;
    }

}

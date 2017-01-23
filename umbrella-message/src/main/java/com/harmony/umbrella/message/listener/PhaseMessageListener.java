package com.harmony.umbrella.message.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import com.harmony.umbrella.message.MessageTrackers;

/**
 * @author wuxii@foxmail.com
 */
public class PhaseMessageListener implements MessageListener {

    private MessageTrackers messageTrackers;
    private MessageListener messageListener;

    public PhaseMessageListener() {
    }

    public PhaseMessageListener(MessageTrackers trackers, MessageListener messageListener) {
        this.messageTrackers = trackers;
        this.messageListener = messageListener;
    }

    @Override
    public void onMessage(Message message) {
        boolean hasTracker = messageTrackers != null;
        try {
            if (hasTracker) {
                messageTrackers.onBeforeConsume(message);
            }
            messageListener.onMessage(message);
            if (hasTracker) {
                messageTrackers.onAfterConsume(message);
            }
        } catch (Exception e) {
            if (hasTracker && e instanceof JMSException) {
                messageTrackers.onConsumeException(message, e);
            }
            throw e;
        }
    }

    public MessageTrackers getMessageTrackers() {
        return messageTrackers;
    }

    public void setMessageTrackers(MessageTrackers messageTrackers) {
        this.messageTrackers = messageTrackers;
    }

    public MessageListener getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

}

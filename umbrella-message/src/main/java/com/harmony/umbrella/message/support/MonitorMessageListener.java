package com.harmony.umbrella.message.support;

import javax.jms.Message;
import javax.jms.MessageListener;

import com.harmony.umbrella.message.MessageMonitor;
import com.harmony.umbrella.message.MessageUtils;
import com.harmony.umbrella.message.MessageMonitor.EventPhase;

/**
 * 
 * @author wuxii@foxmail.com
 */
public class MonitorMessageListener implements MessageListener {

    private MessageListener listener;

    private MessageMonitor monitor;

    public MonitorMessageListener() {
    }

    public MonitorMessageListener(MessageListener listener, MessageMonitor monitor) {
        this.listener = listener;
        this.monitor = monitor;
    }

    @Override
    public void onMessage(Message message) {
        try {
            this.fireEvent(message, EventPhase.BEFORE_CONSUME);
            listener.onMessage(message);
            this.fireEvent(message, EventPhase.AFTER_CONSUME);
        } catch (Exception e) {
            this.fireEvent(message, EventPhase.CONSUME_FAILURE, e);
            throw e;
        }
    }

    protected void fireEvent(final Message message, EventPhase phase) {
        fireEvent(message, phase, null);
    }

    protected void fireEvent(final Message message, EventPhase phase, Throwable e) {
        try {
            monitor.onEvent(MessageUtils.createMessageEvent(message, phase, e));
        } catch (Throwable ex) {
        }
    }

    @Override
    public String toString() {
        return "monitor " + listener;
    }
}
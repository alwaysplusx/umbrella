package com.harmony.umbrella.message.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.harmony.umbrella.message.MessageEventListener;

/**
 * @author wuxii@foxmail.com
 */
public class MessageEventListenerComposite implements MessageEventListener {

    private List<MessageEventListener> messageEventListeners = new ArrayList<>();

    public MessageEventListenerComposite() {
    }

    public MessageEventListenerComposite(MessageEventListener... messageEventListeners) {
        this.setMessageEventListeners(Arrays.asList(messageEventListeners));
    }

    public MessageEventListenerComposite(List<MessageEventListener> messageEventListeners) {
        this.messageEventListeners = messageEventListeners;
    }

    @Override
    public void onEvent(MessageEvent event) {
        for (MessageEventListener listener : messageEventListeners) {
            listener.onEvent(event);
        }
    }

    public List<MessageEventListener> getMessageEventListeners() {
        return messageEventListeners;
    }

    public void setMessageEventListeners(List<MessageEventListener> messageEventListeners) {
        this.messageEventListeners.clear();
        this.messageEventListeners.addAll(messageEventListeners);
    }

}

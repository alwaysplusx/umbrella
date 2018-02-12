package com.harmony.umbrella.message.support;

import javax.jms.Message;

import com.harmony.umbrella.message.MessageMonitor;

/**
 * @author wuxii@foxmail.com
 */
public class MessageEventListener implements MessageMonitor {

    @Override
    public final void onEvent(MessageEvent event) {
        EventPhase eventPhase = event.getEventPhase();
        Message message = event.getMessage();
        switch (eventPhase) {
        case AFTER_CONSUME:
            onConsumeSuccess(message, event);
            break;
        case AFTER_SEND:
            onSendSuccess(message, event);
            break;
        case BEFORE_CONSUME:
            onBeforeConsme(message, event);
            break;
        case BEFORE_SEND:
            onBeforeSend(message, event);
            break;
        case CONSUME_FAILURE:
            onConsumeFailure(message, event.getException(), event);
            break;
        case SEND_FAILURE:
            onSendFailure(message, event.getException(), event);
            break;
        default:
            onUnknowEvent(message, event);
            break;
        }
    }

    protected void onBeforeSend(Message message, MessageEvent event) {
    }

    protected void onSendSuccess(Message message, MessageEvent event) {
    }

    protected void onSendFailure(Message message, Throwable exception, MessageEvent event) {
    }

    protected void onBeforeConsme(Message message, MessageEvent event) {
    }

    protected void onConsumeSuccess(Message message, MessageEvent event) {
    }

    protected void onConsumeFailure(Message message, Throwable exception, MessageEvent event) {
    }

    protected void onUnknowEvent(Message message, MessageEvent event) {
    }

}

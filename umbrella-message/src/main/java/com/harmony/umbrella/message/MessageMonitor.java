package com.harmony.umbrella.message;

import javax.jms.Destination;
import javax.jms.Message;

/**
 * 消息事件监听, 其{@linkplain #onEvent(MessageEvent)}的运行不会干扰的主业务方法, 在代码层面其{@linkplain #onEvent(MessageEvent)}完全是运行在沙盒中
 * 
 * @author wuxii@foxmail.com
 */
public interface MessageMonitor {

    void onEvent(MessageEvent event);

    public interface MessageEvent {

        String getMessageId();

        String getJmsMessageId();

        Message getMessage();

        Destination getDestination();

        EventPhase getEventPhase();

        int getMessageStatus();

        Throwable getException();

    }

    public enum EventPhase {

        BEFORE_SEND, AFTER_SEND, SEND_FAILURE, BEFORE_CONSUME, AFTER_CONSUME, CONSUME_FAILURE;

    }

}

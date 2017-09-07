package com.harmony.umbrella.message;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Topic;

/**
 * @author wuxii@foxmail.com
 */
public interface MessageEventListener {

    void onEvent(MessageEvent event);

    public interface MessageEvent {

        Message getMessage();

        EventPhase getEventPhase();

        DestinationType getDestinationType();

    }

    public enum DestinationType {

        QUEUE(Queue.class), TOPIC(Topic.class);

        private Class<? extends Destination> type;

        private DestinationType(Class<? extends Destination> type) {
            this.type = type;
        }

        public static DestinationType forType(Class<?> type) {
            for (DestinationType t : values()) {
                if (t.type.isAssignableFrom(type)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("unknow destination type");
        }

    }

    public enum EventPhase {

        BEFORE_SEND, AFTER_SEND, SEND_FAILURE, BEFORE_CONSUME, AFTER_CONSUME, CONSUME_FAILURE;

    }

}

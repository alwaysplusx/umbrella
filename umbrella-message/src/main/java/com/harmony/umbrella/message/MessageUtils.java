package com.harmony.umbrella.message;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import com.harmony.umbrella.message.MessageMonitor.DestinationType;
import com.harmony.umbrella.message.MessageMonitor.EventPhase;
import com.harmony.umbrella.message.MessageMonitor.MessageEvent;
import com.harmony.umbrella.message.annotation.MessageSelector;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class MessageUtils {

    public static String getMessageSelector(Class<? extends MessageListener> clazz) {
        String messageSelector = null;
        MessageSelector ann = clazz.getClass().getAnnotation(MessageSelector.class);
        if (TypedMessageListener.class.isAssignableFrom(clazz)) {

        } else if (ann != null) {
            if (StringUtils.isNotBlank(ann.value())) {
                messageSelector = ann.value();
            } else if (ann.type() != Void.class) {
                messageSelector = ann.type().getName();
            }
        }

        return messageSelector == null ? null : MessageTemplate.OBJECT_TYPE_MESSAGE_SELECTOR_KEY + " = '" + messageSelector + "'";
    }

    public static MessageEvent createMessageEvent(Message message, EventPhase phase) throws JMSException {
        return createMessageEvent(message, phase, null);
    }

    public static MessageEvent createMessageEvent(Message message, EventPhase phase, Throwable exception) throws JMSException {
        Destination dest = message.getJMSDestination();
        DestinationType destType = null;
        if (dest != null) {
            destType = DestinationType.forType(dest.getClass());
        }
        return new MessageEventImpl(message, destType, phase, exception);
    }

    private static final class MessageEventImpl implements MessageEvent {

        private Message message;
        private EventPhase phase;
        private DestinationType destType;
        private Throwable exception;

        public MessageEventImpl(Message message, DestinationType destType, EventPhase phase, Throwable exception) {
            this.message = message;
            this.phase = phase;
            this.destType = destType;
            this.exception = exception;
        }

        @Override
        public Message getMessage() {
            return message;
        }

        @Override
        public EventPhase getEventPhase() {
            return phase;
        }

        @Override
        public DestinationType getDestinationType() {
            return destType;
        }

        @Override
        public Throwable getException() {
            return exception;
        }

    }
}

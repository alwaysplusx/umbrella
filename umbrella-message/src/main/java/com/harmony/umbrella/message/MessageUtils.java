package com.harmony.umbrella.message;

import java.util.Properties;
import java.util.UUID;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.util.ClassUtils;

import com.harmony.umbrella.message.MessageMonitor.EventPhase;
import com.harmony.umbrella.message.MessageMonitor.MessageEvent;
import com.harmony.umbrella.message.annotation.MessageSelector;
import com.harmony.umbrella.util.GenericUtils;
import com.harmony.umbrella.util.GenericUtils.GenericTree;
import com.harmony.umbrella.util.GenericUtils.GenericTree.Generic;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class MessageUtils {

    protected static final boolean ACTIVEMQ_PERSENT = ClassUtils.isPresent("org.apache.activemq.ActiveMQConnectionFactory", ClassUtils.getDefaultClassLoader());

    public static Message createMessage(JMSContext jmsContext, MessageType messageType) {
        if (messageType == null) {
            throw new IllegalArgumentException("message type must not null");
        }
        switch (messageType) {
        case BytesMessage:
            return jmsContext.createBytesMessage();
        case MapMessage:
            return jmsContext.createMapMessage();
        case ObjectMessage:
            return jmsContext.createObjectMessage();
        case StreamMessage:
            return jmsContext.createStreamMessage();
        case TextMessage:
            return jmsContext.createTextMessage();
        default:
            throw new MessageException("unknow message type " + messageType);
        }
    }

    public static JmsResources createContainerJmsResources(String connectionFactory, String destination) {
        return createContainerJmsResources(connectionFactory, destination, null);
    }

    public static JmsResources createContainerJmsResources(String connectionFactory, String destination, Properties contextProperties) {
        if (StringUtils.isBlank(connectionFactory)) {
            throw new IllegalArgumentException("connectionFactory is null");
        }
        if (StringUtils.isBlank(destination)) {
            throw new IllegalArgumentException("destination is null");
        }
        if (contextProperties == null) {
            contextProperties = new Properties();
        }
        try {
            InitialContext ctx = new InitialContext(contextProperties);
            ConnectionFactory cf = (ConnectionFactory) ctx.lookup(connectionFactory);
            Destination dest = (Destination) ctx.lookup(destination);
            return new JmsResources(cf, dest);
        } catch (NamingException e) {
            throw new MessageException("create container jms resource failed", e);
        }
    }

    public static JmsResources createActiveMQJmsResources(String brokerUrl, String destination) {
        if (StringUtils.isBlank(brokerUrl)) {
            throw new IllegalArgumentException("brokerUrl is null");
        }
        if (StringUtils.isBlank(destination)) {
            throw new IllegalArgumentException("destination is null");
        }
        return createActiveMQJmsResources(brokerUrl, destination, destination.toLowerCase().indexOf("topic") == -1);
    }

    public static JmsResources createActiveMQJmsResources(String brokerUrl, String destination, boolean queue) {
        if (!ACTIVEMQ_PERSENT) {
            throw new MessageException("create activemq jms resource failed, because ActiveMQ not persent in classpath");
        }
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(brokerUrl);
        ActiveMQDestination dest = queue ? new ActiveMQQueue(destination) : new ActiveMQTopic(destination);
        return new JmsResources(cf, dest);
    }

    public static String getMessageSelector(Class<? extends MessageListener> clazz) {
        String messageSelector = null;
        MessageSelector ann = clazz.getAnnotation(MessageSelector.class);
        if (TypedMessageListener.class.isAssignableFrom(clazz)) {
            GenericTree tree = GenericUtils.parse(clazz);
            Generic generic = tree.getTargetGeneric(TypedMessageListener.class, 0);
            if (generic.getJavaType() != null) {
                messageSelector = generic.getJavaType().getName();
            }
        } else if (ann != null) {
            if (StringUtils.isNotBlank(ann.value())) {
                messageSelector = ann.value();
            } else if (ann.type() != Void.class) {
                messageSelector = ann.type().getName();
            }
        }
        return messageSelector == null ? null : MessageTemplate.TEMPLATE_MESSAGE_OBJECT_TYPE + " = '" + messageSelector + "'";
    }

    public static String newMessageId() {
        return UUID.randomUUID().toString();
    }

    public static MessageEvent createMessageEvent(Message message, EventPhase phase) throws JMSException {
        return createMessageEvent(message, phase, null);
    }

    public static MessageEvent createMessageEvent(Message message, EventPhase phase, Throwable exception) throws JMSException {
        Destination dest = message.getJMSDestination();
        return new MessageEventImpl(message, dest, phase, exception);
    }

    private static final class MessageEventImpl implements MessageEvent {

        private Message message;
        private EventPhase phase;
        private Destination destination;
        private Throwable exception;

        public MessageEventImpl(Message message, Destination destination, EventPhase phase, Throwable exception) {
            this.message = message;
            this.phase = phase;
            this.destination = destination;
            this.exception = exception;
        }

        @Override
        public String getMessageId() {
            String messageId = null;
            try {
                messageId = message.getStringProperty(MessageTemplate.TEMPLATE_MESSAGE_ID);
            } catch (JMSException e) {
            }
            if (messageId == null) {
                try {
                    messageId = newMessageId();
                    message.setStringProperty(MessageTemplate.TEMPLATE_MESSAGE_ID, messageId);
                } catch (JMSException e) {
                    // can't set property return null
                    return null;
                }
            }
            return messageId;
        }

        @Override
        public String getJmsMessageId() {
            try {
                return message.getJMSMessageID();
            } catch (JMSException e) {
            }
            return null;
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
        public int getMessageStatus() {
            return phase.ordinal();
        }

        @Override
        public Destination getDestination() {
            return destination;
        }

        @Override
        public Throwable getException() {
            return exception;
        }

    }
}

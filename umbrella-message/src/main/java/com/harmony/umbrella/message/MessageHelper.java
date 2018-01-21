package com.harmony.umbrella.message;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;

import org.springframework.util.ReflectionUtils;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.message.JmsTemplate.SessionPoint;
import com.harmony.umbrella.message.MessageMonitor.EventPhase;
import com.harmony.umbrella.message.MessageMonitor.MessageEvent;
import com.harmony.umbrella.message.annotation.MessageSelector;
import com.harmony.umbrella.message.creator.BytesMessageCreator;
import com.harmony.umbrella.message.creator.MapMessageCreator;
import com.harmony.umbrella.message.creator.ObjectMessageCreator;
import com.harmony.umbrella.message.creator.StreamMessageCreator;
import com.harmony.umbrella.message.creator.TextMessageCreator;
import com.harmony.umbrella.message.support.MonitorMessageListener;
import com.harmony.umbrella.message.support.SimpleDynamicMessageListener;
import com.harmony.umbrella.util.StringUtils;

/**
 * 消息发送helper
 * 
 * @author wuxii@foxmail.com
 */
public class MessageHelper implements MessageTemplate {

    private static final Log log = Logs.getLog(MessageHelper.class);

    private JmsTemplate jmsTemplate;
    private MessageMonitor messageMonitor;

    public MessageHelper() {
    }

    public MessageHelper(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    /**
     * 发送bytes message
     * 
     * @param buf
     *            bytes @
     */
    @Override
    public void sendBytesMessage(byte[] buf) {
        sendMessage(new BytesMessageCreator(buf));
    }

    @Override
    public void sendObjectMessage(Serializable obj) {
        sendMessage(new InternalObjectMessageCreator(obj));
    }

    @Override
    public void sendMapMessage(Map map) {
        sendMapMessage(map, false);
    }

    @Override
    public void sendMapMessage(Map map, boolean skipNotStatisfiedEntry) {
        sendMessage(new MapMessageCreator(map, skipNotStatisfiedEntry));
    }

    @Override
    public void sendTextMessage(String text) {
        sendMessage(new TextMessageCreator(text));
    }

    @Override
    public void sendStreamMessage(InputStream is) {
        sendMessage(new StreamMessageCreator(is));
    }

    /**
     * 发送消息, 通过定制的消息{@linkplain MessageCreator}creator来创建定制化的消息
     * 
     * @param creator
     *            定制化消息创建器 @
     */
    @Override
    public void sendMessage(MessageCreator messageCreator) {
        sendMessage(null, messageCreator, null);
    }

    @Override
    public void sendMessage(Destination destination, MessageCreator messageCreator, MessageProducerConfigure configure) {
        SessionPoint session = jmsTemplate.newSession();
        Message message = null;
        destination = destination == null ? session.getDestination() : destination;
        try {
            message = messageCreator.newMessage(session.getSession());
            message.setJMSDestination(destination);
            message.setJMSTimestamp(System.currentTimeMillis());

            MessageProducer producer = session.getMessageProducer();
            if (configure != null) {
                configure.config(producer);
            }

            fireEvent(message, EventPhase.BEFORE_SEND);
            producer.send(destination, message);
            fireEvent(message, EventPhase.AFTER_SEND);

        } catch (Throwable e) {
            fireEvent(message, EventPhase.SEND_FAILURE, e);
            session.rollback();
            if (e instanceof JMSException) {
                throw new MessageException("send message failed", e);
            }
            ReflectionUtils.rethrowRuntimeException(e);
        } finally {
            session.release();
        }
    }

    @Override
    public Message receiveMessage() {
        return receiveMessage(-1);
    }

    @Override
    public Message receiveMessage(long timeout) {
        SessionPoint session = jmsTemplate.newSession();
        try {
            MessageConsumer consumer = session.getMessageConsumer();
            Message message = timeout < 0 ? consumer.receiveNoWait() : consumer.receive(timeout);
            return message;
        } catch (JMSException e) {
            session.rollback();
            throw new MessageException("receive message failed", e);
        } finally {
            session.release();
        }
    }

    @Override
    public void setMessageMonitor(MessageMonitor monitor) {
        this.messageMonitor = monitor;
    }

    @Override
    public MessageMonitor getMessageMonitor() {
        return messageMonitor;
    }

    @Override
    public DynamicMessageListener startMessageListener(MessageListener listener) {
        String messageSelector = null;
        if (listener instanceof TypedMessageListener) {
            messageSelector = OBJECT_TYPE_MESSAGE_SELECTOR_KEY + " = '" + ((TypedMessageListener) listener).getType().getName() + "'";
        } else if (listener.getClass().getAnnotation(MessageSelector.class) != null) {
            MessageSelector ann = listener.getClass().getAnnotation(MessageSelector.class);
            if (StringUtils.isNotBlank(ann.value())) {
                messageSelector = ann.value();
            } else if (ann.type() != Void.class) {
                messageSelector = ann.type().getName();
            }
            if (messageSelector != null) {
                messageSelector = OBJECT_TYPE_MESSAGE_SELECTOR_KEY + " = '" + messageSelector + "'";
            }
        } else {
            messageSelector = jmsTemplate.getMessageSelector();
        }
        return startMessageListener(messageSelector, listener);
    }

    @Override
    public DynamicMessageListener startMessageListener(String messageSelector, MessageListener listener) {
        SessionPoint session = jmsTemplate.newSession(jmsTemplate.getDestination(), messageSelector);
        SimpleDynamicMessageListener dml = new SimpleDynamicMessageListener(session);
        dml.setSessionAutoCommit(jmsTemplate.isSessionAutoCommit());
        if (messageMonitor != null) {
            listener = new MonitorMessageListener(listener, messageMonitor);
        }
        dml.setMessageListener(listener);
        dml.start();
        return dml;
    }

    private void fireEvent(Message message, EventPhase phase) {
        fireEvent(message, phase, null);
    }

    private void fireEvent(Message message, EventPhase phase, Throwable exception) {
        if (messageMonitor != null) {
            MessageEvent event = null;
            try {
                event = MessageUtils.createMessageEvent(message, phase, exception);
                messageMonitor.onEvent(event);
            } catch (Throwable e) {
                log.debug("fire message failed, event {}", event);
            }
        }
    }

    @Override
    public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public Destination getDestination() {
        return jmsTemplate.getDestination();
    }

    public ConnectionFactory getConnectionFactory() {
        return jmsTemplate.getConnectionFactory();
    }

    private static final class InternalObjectMessageCreator extends ObjectMessageCreator {

        private static final long serialVersionUID = 8358533485060344548L;

        public InternalObjectMessageCreator(Serializable object) {
            super(object);
        }

        @Override
        protected void doMapping(ObjectMessage message) throws JMSException {
            super.doMapping(message);
            message.setStringProperty(OBJECT_TYPE_MESSAGE_SELECTOR_KEY, object.getClass().getName());
        }

    }

}

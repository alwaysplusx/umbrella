package com.harmony.umbrella.message;

import com.harmony.umbrella.message.JmsTemplate.SessionPoint;
import com.harmony.umbrella.message.MessageMonitor.EventPhase;
import com.harmony.umbrella.message.MessageMonitor.MessageEvent;
import com.harmony.umbrella.message.creator.*;
import com.harmony.umbrella.message.support.MonitorMessageListener;
import com.harmony.umbrella.message.support.SimpleDynamicMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import javax.jms.*;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

/**
 * 消息发送helper
 *
 * @author wuxii@foxmail.com
 */
@Slf4j
public class MessageHelper implements MessageTemplate {

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
     * @param buf bytes @
     */
    @Override
    public String sendBytesMessage(byte[] buf) {
        return sendMessage(new BytesMessageCreator(buf));
    }

    @Override
    public String sendObjectMessage(Serializable obj) {
        return sendMessage(new InternalObjectMessageCreator(obj));
    }

    @Override
    public String sendMapMessage(Map map) {
        return sendMapMessage(map, false);
    }

    @Override
    public String sendMapMessage(Map map, boolean skipNotStatisfiedEntry) {
        return sendMessage(new MapMessageCreator(map, skipNotStatisfiedEntry));
    }

    @Override
    public String sendTextMessage(String text) {
        return sendMessage(new TextMessageCreator(text));
    }

    @Override
    public String sendStreamMessage(InputStream is) {
        return sendMessage(new StreamMessageCreator(is));
    }

    /**
     * 发送消息, 通过定制的消息{@linkplain MessageCreator}creator来创建定制化的消息
     *
     * @param messageCreator 定制化消息创建器
     */
    @Override
    public String sendMessage(MessageCreator messageCreator) {
        return sendMessage(null, messageCreator, null);
    }

    @Override
    public String sendMessage(Destination destination, MessageCreator messageCreator, MessageProducerConfigure configure) {
        SessionPoint session = jmsTemplate.newSession();
        Message message = null;
        String messageId = null;
        destination = destination == null ? session.getDestination() : destination;
        try {
            message = messageCreator.createMessage(session.getSession());
            message.setJMSDestination(destination);
            message.setJMSTimestamp(System.currentTimeMillis());

            MessageProducer producer = session.getMessageProducer();
            if (configure != null) {
                configure.config(producer);
            }
            // set custom message id
            messageId = message.getStringProperty(TEMPLATE_MESSAGE_ID);
            if (messageId == null) {
                messageId = nextMessageId();
                message.setStringProperty(TEMPLATE_MESSAGE_ID, messageId);
            }

            // fire event
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
        return messageId;
    }

    @Override
    public Message receiveMessage() {
        return receiveMessage(-1);
    }

    @Override
    public Message receiveMessage(String messageId) {
        return receiveMessage(messageId, -1, false);
    }

    @Override
    public Message receiveMessage(String messageId, long timeout) {
        return receiveMessage(messageId, timeout, false);
    }

    @Override
    public Message receiveMessage(long timeout) {
        return receiveMessage(null, timeout, true);
    }

    @Override
    public Message receiveMessage(long timeout, boolean quiet) {
        return receiveMessage(null, timeout, quiet);
    }

    protected Message receiveMessage(String messageId, long timeout, boolean quiet) {
        String messageSelector = null;
        if (messageId != null) {
            messageSelector = TEMPLATE_MESSAGE_ID + " = '" + messageId + "'";
        }
        SessionPoint session = jmsTemplate.newSession(messageSelector);
        try {
            MessageConsumer consumer = session.getMessageConsumer();
            Message message = timeout < 0 ? consumer.receiveNoWait() : consumer.receive(timeout);
            if (message != null && !quiet) {
                this.fireEvent(message, EventPhase.AFTER_CONSUME);
            }
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
        String messageSelector = MessageUtils.getMessageSelector(listener.getClass());
        if (messageSelector == null) {
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

    protected String nextMessageId() {
        return MessageUtils.newMessageId();
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
                log.warn("fire message failed {}, event {}", e.getMessage(), event);
                if (log.isDebugEnabled()) {
                    log.warn("", e);
                }
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
            message.setStringProperty(TEMPLATE_MESSAGE_OBJECT_TYPE, object.getClass().getName());
        }

    }

}

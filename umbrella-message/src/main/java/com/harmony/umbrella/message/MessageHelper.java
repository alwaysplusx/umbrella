package com.harmony.umbrella.message;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import com.harmony.umbrella.message.MessageEventListener.DestinationType;
import com.harmony.umbrella.message.MessageEventListener.EventPhase;
import com.harmony.umbrella.message.MessageEventListener.MessageEvent;
import com.harmony.umbrella.message.creator.BytesMessageCreator;
import com.harmony.umbrella.message.creator.MapMessageCreator;
import com.harmony.umbrella.message.creator.ObjectMessageCreator;
import com.harmony.umbrella.message.creator.StreamMessageCreator;
import com.harmony.umbrella.message.creator.TextMessageCreator;
import com.harmony.umbrella.message.listener.SimpleDynamicMessageListener;
import com.harmony.umbrella.message.support.SimpleJmsTemplate;

/**
 * 消息发送helper
 * 
 * @author wuxii@foxmail.com
 */
public class MessageHelper implements MessageTemplate {

    ConnectionFactory connectionFactory;
    Destination destination;

    String username;
    String password;
    boolean transacted;
    int acknowledgeMode;

    MessageEventListener messageEventListener;
    SimpleDynamicMessageListener messageListener;
    boolean sessionAutoCommit;
    boolean autoStartListener;

    public MessageHelper() {
    }

    /**
     * 发送bytes message
     * 
     * @param buf
     *            bytes
     * @throws JMSException
     */
    @Override
    public void sendBytesMessage(byte[] buf) throws JMSException {
        sendMessage(new BytesMessageCreator(buf));
    }

    @Override
    public void sendObjectMessage(Serializable obj) throws JMSException {
        sendObjectMessage(new ObjectMessageCreator(obj));
    }

    @Override
    public void sendMapMessage(Map map) throws JMSException {
        sendMapMessage(map, false);
    }

    @Override
    public void sendMapMessage(Map map, boolean skipNotStatisfiedEntry) throws JMSException {
        sendMessage(new MapMessageCreator(map, skipNotStatisfiedEntry));
    }

    @Override
    public void sendTextMessage(String text) throws JMSException {
        sendMessage(new TextMessageCreator(text));
    }

    @Override
    public void sendStreamMessage(InputStream is) throws JMSException {
        sendMessage(new StreamMessageCreator(is));
    }

    @Override
    public void sendAnyMessage(AnyMessage msg) throws JMSException {
        sendMessage(new MessageCreator() {

            private static final long serialVersionUID = -2000900025458386923L;

            @Override
            public Message message(Session session) throws JMSException {
                BytesMessage message = session.createBytesMessage();
                message.writeBytes(msg.getBufferArray());
                message.setStringProperty(AnyMessage.ANY_MESSAGE_HEADER, msg.getType());
                return message;
            }

        });
    }

    /**
     * 发送消息, 通过定制的消息{@linkplain MessageCreator}creator来创建定制化的消息
     * 
     * @param creator
     *            定制化消息创建器
     * @throws JMSException
     */
    @Override
    public void sendMessage(MessageCreator creator) throws JMSException {
        JmsTemplate jmsTemplate = getJmsTemplate();
        Message message = null;
        try {
            jmsTemplate.start();
            Session session = jmsTemplate.getSession();
            MessageProducer producer = jmsTemplate.getMessageProducer();
            message = creator.message(session);
            message.setJMSDestination(jmsTemplate.getDestination());
            message.setJMSTimestamp(System.currentTimeMillis());
            fireEvent(message, EventPhase.BEFORE_SEND);
            producer.send(message);
            fireEvent(message, EventPhase.AFTER_SEND);
        } catch (Exception e) {
            fireEvent(message, EventPhase.SEND_FAILURE);
            jmsTemplate.rollback();
            throw e;
        } finally {
            jmsTemplate.stop();
        }
    }

    @Override
    public Message receiveMessage() throws JMSException {
        return receiveMessage(-1);
    }

    @Override
    public Message receiveMessage(long timeout) throws JMSException {
        JmsTemplate jmsTemplate = getJmsTemplate();
        try {
            jmsTemplate.start();
            MessageConsumer consumer = jmsTemplate.getMessageConsumer();
            Message message = timeout < 0 ? consumer.receiveNoWait() : consumer.receive(timeout);
            return message;
        } catch (JMSException e) {
            jmsTemplate.rollback();
            throw e;
        } finally {
            jmsTemplate.stop();
        }
    }

    @Override
    public void setMessageListener(MessageListener listener) {
        if (messageListener.isStarted()) {
            throw new IllegalStateException("message listener already started");
        }
        if (messageListener != null) {
            messageListener.setMessageListener(listener);
        } else {
            messageListener = wrap(listener);
        }
        if (autoStartListener) {
            try {
                startMessageListener();
            } catch (JMSException e) {
                new IllegalStateException("can't start message listener " + messageListener, e);
            }
        }
    }

    @Override
    public void setMessageEventListener(MessageEventListener eventListener) {
        if (messageListener.isStarted()) {
            throw new IllegalStateException("message listener already started");
        }
        this.messageEventListener = eventListener;
    }

    @Override
    public void startMessageListener() throws JMSException {
        if (messageListener == null) {
            throw new IllegalStateException("message listener not set");
        }
        this.messageListener.start();
    }

    @Override
    public void stopMessageListener() throws JMSException {
        stopMessageListener(false);
    }

    @Override
    public void stopMessageListener(boolean remove) throws JMSException {
        if (messageListener != null) {
            messageListener.stop();
            if (remove) {
                messageListener = null;
            }
        }
    }

    @Override
    public MessageListener getMessageListener() {
        MessageListener result = messageListener;
        while (true) {
            if (result instanceof SimpleDynamicMessageListener) {
                result = ((SimpleDynamicMessageListener) result).getMessageListener();
            } else if (result instanceof MessageEventListenerAdapter) {
                result = ((MessageEventListenerAdapter) result).messageListener;
            } else {
                break;
            }
        }
        return result;
    }

    @Override
    public JmsTemplate getJmsTemplate() {
        if (connectionFactory == null || destination == null) {
            throw new IllegalArgumentException("connection factory or destination is null");
        }
        SimpleJmsTemplate jmsTemplate = new SimpleJmsTemplate(connectionFactory, destination);
        jmsTemplate.setUsername(username);
        jmsTemplate.setPassword(password);
        jmsTemplate.setSessionAutoCommit(sessionAutoCommit);
        jmsTemplate.setTransacted(transacted);
        jmsTemplate.setAcknowledgeMode(acknowledgeMode);
        return jmsTemplate;
    }

    private void fireEvent(Message message, EventPhase phase) {
        if (messageEventListener != null) {
            try {
                messageEventListener.onEvent(new MessageEventImpl(message, phase));
            } catch (Throwable e) {
            }
        }
    }

    /**
     * 将消息监听包装成动态受监控的消息监听器
     * 
     * @param listener
     *            消息监听器
     * @return 动态受监控的消息监听器
     */
    private SimpleDynamicMessageListener wrap(MessageListener listener) {
        JmsTemplate jmsTemplate = getJmsTemplate();
        if (messageEventListener != null) {
            // 含有event listener则先包装为接受事件的消息监听器
            listener = new MessageEventListenerAdapter(listener, messageEventListener);
        }
        // 包装为可动态开关的消息监听器
        return new SimpleDynamicMessageListener(jmsTemplate, listener);
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isTransacted() {
        return transacted;
    }

    public void setTransacted(boolean transacted) {
        this.transacted = transacted;
    }

    public int getAcknowledgeMode() {
        return acknowledgeMode;
    }

    public void setAcknowledgeMode(int acknowledgeMode) {
        this.acknowledgeMode = acknowledgeMode;
    }

    public boolean isSessionAutoCommit() {
        return sessionAutoCommit;
    }

    public void setSessionAutoCommit(boolean sessionAutoCommit) {
        this.sessionAutoCommit = sessionAutoCommit;
    }

    public boolean isAutoStartListener() {
        return autoStartListener;
    }

    public void setAutoStartListener(boolean autoStartListener) {
        this.autoStartListener = autoStartListener;
    }

    public void setMessageListener(SimpleDynamicMessageListener messageListener) {
        this.messageListener = messageListener;
    }

    private static class MessageEventListenerAdapter implements MessageListener {

        private MessageListener messageListener;
        private MessageEventListener messageEventListener;

        public MessageEventListenerAdapter(MessageListener messageListener, MessageEventListener messageEventListener) {
            this.messageListener = messageListener;
            this.messageEventListener = messageEventListener;
        }

        @Override
        public void onMessage(Message message) {
            try {
                this.fireEvent(message, EventPhase.BEFORE_CONSUME);
                messageListener.onMessage(message);
                this.fireEvent(message, EventPhase.AFTER_CONSUME);
            } catch (Exception e) {
                this.fireEvent(message, EventPhase.CONSUME_FAILURE);
                throw e;
            }
        }

        protected void fireEvent(final Message message, EventPhase phase) {
            try {
                messageEventListener.onEvent(new MessageEventImpl(message, phase));
            } catch (Throwable e) {
            }
        }
    }

    private static final class MessageEventImpl implements MessageEvent {

        private Message message;
        private EventPhase phase;
        private DestinationType destType;

        public MessageEventImpl(Message message, EventPhase phase) {
            this.message = message;
            this.phase = phase;
            this.destType = DestinationType.forType(message.getClass());
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

    }

}

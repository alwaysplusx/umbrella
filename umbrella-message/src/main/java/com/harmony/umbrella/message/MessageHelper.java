package com.harmony.umbrella.message;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.message.MessageMonitor.DestinationType;
import com.harmony.umbrella.message.MessageMonitor.EventPhase;
import com.harmony.umbrella.message.MessageMonitor.MessageEvent;
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

    private static final Log log = Logs.getLog(MessageHelper.class);

    ConnectionFactory connectionFactory;
    Destination destination;

    String username;
    String password;
    boolean transacted;
    int sessionMode;

    MessageMonitor messageMonitor;
    SimpleDynamicMessageListener messageListener;
    ExceptionListener exceptionListener;

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

    /**
     * 发送消息, 通过定制的消息{@linkplain MessageCreator}creator来创建定制化的消息
     * 
     * @param creator
     *            定制化消息创建器
     * @throws JMSException
     */
    @Override
    public void sendMessage(MessageCreator messageCreator) throws JMSException {
        sendMessage(null, messageCreator, null);
    }

    @Override
    public void sendMessage(Destination destination, MessageCreator messageCreator, MessageProducerConfigure configure) throws JMSException {
        JmsTemplate jmsTemplate = getJmsTemplate();
        Message message = null;
        destination = destination == null ? jmsTemplate.getDestination() : destination;
        try {
            jmsTemplate.start();
            Session session = jmsTemplate.getSession();
            message = messageCreator.newMessage(session);
            message.setJMSDestination(destination);
            message.setJMSTimestamp(System.currentTimeMillis());

            MessageProducer producer = jmsTemplate.getMessageProducer();
            if (configure != null) {
                configure.config(producer);
            }

            fireEvent(message, EventPhase.BEFORE_SEND);
            producer.send(destination, message);
            fireEvent(message, EventPhase.AFTER_SEND);

        } catch (Throwable e) {
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
        if (messageListener != null && messageListener.isStarted()) {
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
    public void setMessageMonitor(MessageMonitor monitor) {
        this.messageMonitor = monitor;
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
            } else if (result instanceof MessageListenerMonitorAdapter) {
                result = ((MessageListenerMonitorAdapter) result).messageListener;
            } else {
                break;
            }
        }
        return result;
    }

    public JmsTemplate getJmsTemplate() {
        if (connectionFactory == null || destination == null) {
            throw new IllegalArgumentException("connection factory or destination is null");
        }
        SimpleJmsTemplate jmsTemplate = new SimpleJmsTemplate(connectionFactory, destination);
        jmsTemplate.setUsername(username);
        jmsTemplate.setPassword(password);
        jmsTemplate.setSessionAutoCommit(sessionAutoCommit);
        jmsTemplate.setTransacted(transacted);
        jmsTemplate.setSessionMode(sessionMode);
        jmsTemplate.setExceptionListener(exceptionListener);
        return jmsTemplate;
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
        return new SimpleDynamicMessageListener(jmsTemplate, new MessageListenerMonitorAdapter(listener));
    }

    private void fireEvent(Message message, EventPhase phase) {
        fireEvent(message, phase, null);
    }

    private void fireEvent(Message message, EventPhase phase, Throwable exception) {
        if (messageMonitor != null) {
            MessageEventImpl event = null;
            try {
                Destination dest = message.getJMSDestination();
                DestinationType destType = null;
                if (dest != null) {
                    destType = DestinationType.forType(dest.getClass());
                }
                event = new MessageEventImpl(message, destType, phase, exception);
                messageMonitor.onEvent(event);
            } catch (Throwable e) {
                log.debug("fire message failed, event {}", event);
            }
        }
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

    public int getSessionMode() {
        return sessionMode;
    }

    public void setSessionMode(int sessionMode) {
        this.sessionMode = sessionMode;
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

    private class MessageListenerMonitorAdapter implements MessageListener {

        private MessageListener messageListener;

        public MessageListenerMonitorAdapter(MessageListener messageListener) {
            this.messageListener = messageListener;
        }

        @Override
        public void onMessage(Message message) {
            try {
                fireEvent(message, EventPhase.BEFORE_CONSUME);
                messageListener.onMessage(message);
                fireEvent(message, EventPhase.AFTER_CONSUME);
            } catch (Throwable e) {
                fireEvent(message, EventPhase.CONSUME_FAILURE, e);
                throw e;
            }
        }

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

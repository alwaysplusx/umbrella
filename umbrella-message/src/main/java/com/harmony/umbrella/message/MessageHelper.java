package com.harmony.umbrella.message;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import com.harmony.umbrella.message.listener.PhaseMessageListener;
import com.harmony.umbrella.message.listener.SimpleDynamicMessageListener;
import com.harmony.umbrella.message.tracker.BytesMessageConfiger;
import com.harmony.umbrella.message.tracker.MapMessageConfiger;
import com.harmony.umbrella.message.tracker.ObjectMessageConfiger;
import com.harmony.umbrella.message.tracker.StreamMessageConfiger;
import com.harmony.umbrella.message.tracker.TextMessageConfiger;
import com.harmony.umbrella.util.StringUtils;

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

    MessageTrackers trackers;
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
        sendBytesMessage(buf, null);
    }

    /**
     * 发送bytes message
     * 
     * @param buf
     *            bytes
     * @param appender
     *            消息appender
     * @throws JMSException
     */
    @Override
    public void sendBytesMessage(byte[] buf, MessageAppender<BytesMessage> appender) throws JMSException {
        sendMessage(new BytesMessageConfiger(buf, appender));
    }

    @Override
    public void sendObjectMessage(Serializable obj) throws JMSException {
        sendObjectMessage(obj, null);
    }

    @Override
    public void sendObjectMessage(Serializable obj, MessageAppender<ObjectMessage> appender) throws JMSException {
        sendMessage(new ObjectMessageConfiger(obj, appender));
    }

    @Override
    public void sendMapMessage(Map map) throws JMSException {
        sendMapMessage(map, false, null);
    }

    @Override
    public void sendMapMessage(Map map, boolean skipNotStatisfiedEntry) throws JMSException {
        sendMapMessage(map, skipNotStatisfiedEntry, null);
    }

    @Override
    public void sendMapMessage(Map map, MessageAppender<MapMessage> appender) throws JMSException {
        sendMapMessage(map, false, appender);
    }

    @Override
    public void sendMapMessage(Map map, boolean skipNotStatisfiedEntry, MessageAppender<MapMessage> appender) throws JMSException {
        sendMessage(new MapMessageConfiger(map, skipNotStatisfiedEntry, appender));
    }

    @Override
    public void sendTextMessage(String text) throws JMSException {
        sendTextMessage(text, null);
    }

    @Override
    public void sendTextMessage(String text, MessageAppender<TextMessage> appender) throws JMSException {
        sendMessage(new TextMessageConfiger(text, appender));
    }

    @Override
    public void sendStreamMessage(InputStream is) throws JMSException {
        sendStreamMessage(is, null);
    }

    @Override
    public void sendStreamMessage(InputStream is, MessageAppender<StreamMessage> appender) throws JMSException {
        sendMessage(new StreamMessageConfiger(is, appender));
    }

    /**
     * 发送消息, 通过定制的消息{@linkplain MessageConfiger}configer来创建定制化的消息
     * 
     * @param configer
     *            定制化消息创建器
     * @throws JMSException
     */
    @Override
    public void sendMessage(MessageConfiger configer) throws JMSException {
        JmsTemplate jmsTemplate = getJmsTemplate();
        Message message = null;
        try {
            jmsTemplate.start();
            Session session = jmsTemplate.getSession();
            MessageProducer producer = jmsTemplate.getMessageProducer();
            message = configer.message(session);
            message.setJMSDestination(jmsTemplate.getDestination());
            message.setJMSTimestamp(System.currentTimeMillis());
            trackers.onBeforeSend(message);
            producer.send(message);
            trackers.onAfterSend(message);
        } catch (JMSException e) {
            if (message != null) {
                trackers.onSendException(message, e);
            }
            jmsTemplate.rollback();
            throw e;
        } finally {
            jmsTemplate.stop();
        }
    }

    public void sendMessageQuietly(MessageConfiger configer) throws JMSException {
        JmsTemplate jmsTemplate = getJmsTemplate();
        try {
            jmsTemplate.start();
            Session session = jmsTemplate.getSession();
            MessageProducer producer = jmsTemplate.getMessageProducer();
            Message message = configer.message(session);
            message.setJMSDestination(jmsTemplate.getDestination());
            message.setJMSTimestamp(System.currentTimeMillis());
            producer.send(message);
        } catch (JMSException e) {
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
            } else if (result instanceof PhaseMessageListener) {
                result = ((PhaseMessageListener) result).getMessageListener();
            } else {
                break;
            }
        }
        return result;
    }

    @Override
    public MessageTrackers getMessageTrackers() {
        return trackers;
    }

    @Override
    public JmsTemplate getJmsTemplate() {
        return new HelperJmsTemplate();
    }

    private SimpleDynamicMessageListener wrap(MessageListener listener) {
        JmsTemplate jmsTemplate = getJmsTemplate();
        return new SimpleDynamicMessageListener(jmsTemplate, new PhaseMessageListener(trackers, listener));
    }

    private class HelperJmsTemplate implements JmsTemplate {

        private Connection connection;
        private Session session;
        private MessageProducer messageProducer;
        private MessageConsumer messageConsumer;

        private boolean sessionRrollbacked;
        private boolean sessionCommited;

        @Override
        public boolean isStated() {
            return connection != null;
        }

        @Override
        public void start() throws JMSException {
            if (StringUtils.isNotBlank(username)) {
                this.connection = connectionFactory.createConnection(username, password);
            } else {
                this.connection = connectionFactory.createConnection();
            }
            this.connection.start();
        }

        @Override
        public void stop() {
            if (sessionAutoCommit && !sessionRrollbacked && !sessionCommited) {
                try {
                    commit();
                } catch (JMSException e) {
                    throw new IllegalStateException("session not commit", e);
                }
            }
            if (messageConsumer != null) {
                try {
                    messageConsumer.close();
                } catch (JMSException e) {
                }
                messageConsumer = null;
            }
            if (messageProducer != null) {
                try {
                    messageProducer.close();
                } catch (JMSException e) {
                }
                messageProducer = null;
            }
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                }
                session = null;
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                }
                connection = null;
            }
        }

        @Override
        public Connection getConnection() throws JMSException {
            return connection;
        }

        @Override
        public Session getSession() throws JMSException {
            if (session == null) {
                session = connection.createSession(transacted, acknowledgeMode);
            }
            return session;
        }

        @Override
        public MessageProducer getMessageProducer() throws JMSException {
            if (messageProducer == null) {
                messageProducer = getSession().createProducer(destination);
            }
            return messageProducer;
        }

        @Override
        public MessageConsumer getMessageConsumer() throws JMSException {
            if (messageConsumer == null) {
                messageConsumer = getSession().createConsumer(destination);
            }
            return messageConsumer;
        }

        @Override
        public ConnectionFactory getConnectionFactory() {
            return connectionFactory;
        }

        @Override
        public Destination getDestination() {
            return destination;
        }

        @Override
        public void commit() throws JMSException {
            if (session != null) {
                session.commit();
                sessionCommited = true;
            }
        }

        @Override
        public void rollback() throws JMSException {
            if (session != null) {
                session.rollback();
                sessionRrollbacked = true;
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

    public int getAcknowledgeMode() {
        return acknowledgeMode;
    }

    public void setAcknowledgeMode(int acknowledgeMode) {
        this.acknowledgeMode = acknowledgeMode;
    }

    public MessageTrackers getTrackers() {
        return trackers;
    }

    public void setTrackers(MessageTrackers trackers) {
        this.trackers = trackers;
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

}

package com.harmony.umbrella.message;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.harmony.umbrella.message.listener.PhaseMessageListener;
import com.harmony.umbrella.message.listener.SimpleDynamicMessageListener;
import com.harmony.umbrella.message.tracker.BytesMessageConfiger;
import com.harmony.umbrella.message.tracker.MapMessageConfiger;
import com.harmony.umbrella.message.tracker.ObjectMessageConfiger;
import com.harmony.umbrella.message.tracker.StreamMessageConfiger;
import com.harmony.umbrella.message.tracker.TextMessageConfiger;
import com.harmony.umbrella.util.ClassFilter.ClassFilterFeature;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * 消息发送helper
 * 
 * @author wuxii@foxmail.com
 */
public class MessageHelper {

    private ConnectionFactory connectionFactory;
    private Destination destination;

    // 
    private String username;
    private String password;
    private boolean transacted;
    private int acknowledgeMode;

    protected MessageTrackers trackers;
    private SimpleDynamicMessageListener messageListener;
    private boolean sessionAutoCommit;
    private boolean autoStartListener;

    public MessageHelper() {
    }

    /**
     * 发送bytes message
     * 
     * @param buf
     *            bytes
     * @throws JMSException
     */
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
    public void sendBytesMessage(byte[] buf, MessageAppender<BytesMessage> appender) throws JMSException {
        sendMessage(new BytesMessageConfiger(buf, appender));
    }

    public void sendObjectMessage(Serializable obj) throws JMSException {
        sendObjectMessage(obj, null);
    }

    public void sendObjectMessage(Serializable obj, MessageAppender<ObjectMessage> appender) throws JMSException {
        sendMessage(new ObjectMessageConfiger(obj, appender));
    }

    public void sendMapMessage(Map map) throws JMSException {
        sendMapMessage(map, false, null);
    }

    public void sendMapMessage(Map map, boolean skipNotStatisfiedEntry) throws JMSException {
        sendMapMessage(map, skipNotStatisfiedEntry, null);
    }

    public void sendMapMessage(Map map, MessageAppender<MapMessage> appender) throws JMSException {
        sendMapMessage(map, false, appender);
    }

    public void sendMapMessage(Map map, boolean skipNotStatisfiedEntry, MessageAppender<MapMessage> appender) throws JMSException {
        sendMessage(new MapMessageConfiger(map, skipNotStatisfiedEntry, appender));
    }

    public void sendTextMessage(String text) throws JMSException {
        sendTextMessage(text, null);
    }

    public void sendTextMessage(String text, MessageAppender<TextMessage> appender) throws JMSException {
        sendMessage(new TextMessageConfiger(text, appender));
    }

    public void sendStreamMessage(InputStream is) throws JMSException {
        sendStreamMessage(is, null);
    }

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

    public Message receiveMessage() throws JMSException {
        return receiveMessage(-1);
    }

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

    public void setListener(MessageListener listener) {
        if (messageListener != null) {
            messageListener.setMessageListener(listener);
        } else {
            messageListener = wrap(listener);
        }
        if (autoStartListener) {
            try {
                startListener();
            } catch (JMSException e) {
                new IllegalStateException("can't start message listener " + messageListener, e);
            }
        }
    }

    public void startListener() throws JMSException {
        if (messageListener == null) {
            throw new IllegalStateException("message listener not set");
        }
        if (messageListener.isStarted()) {
            throw new IllegalStateException("listener already started");
        }
        this.messageListener.start();
    }

    public void stopListener() throws JMSException {
        stopListener(false);
    }

    public void stopListener(boolean remove) throws JMSException {
        if (messageListener != null) {
            messageListener.stop();
            if (remove) {
                messageListener = null;
            }
        }
    }

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

    public MessageTrackers getMessageTrackers() {
        return trackers;
    }

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

    public static class MessageHelperBuilder<T extends MessageHelperBuilder<T>> {

        protected ConnectionFactory connectionFactory;
        protected Destination destination;

        protected String username;
        protected String password;
        protected boolean transacted = true;
        protected int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;
        protected boolean sessionAutoCommit = true;
        protected boolean autoStartListener = true;

        protected List<MessageTracker> trackers = new ArrayList<>();

        protected Class<? extends MessageListener> messageListenerClass;
        protected MessageListener messageListener;

        protected String connectionFactoryJNDI;
        protected String destinationJNDI;
        protected Properties contextProperties = new Properties();

        public T username(String username) {
            this.username = username;
            return (T) this;
        }

        public T password(String password) {
            this.password = password;
            return (T) this;
        }

        public T connectionFactory(ConnectionFactory connectionFactory) {
            this.connectionFactory = connectionFactory;
            return (T) this;
        }

        public T destination(Destination destination) {
            this.destination = destination;
            return (T) this;
        }

        public T transacted(boolean transacted) {
            this.transacted = transacted;
            return (T) this;
        }

        public T acknowledgeMode(int acknowledgeMode) {
            this.acknowledgeMode = acknowledgeMode;
            return (T) this;
        }

        public T contextProperties(String name, String value) {
            contextProperties.setProperty(name, value);
            return (T) this;
        }

        public T contextProperties(Properties properties) {
            contextProperties.putAll(properties);
            return (T) this;
        }

        public T connectionFactoryJNDI(String connectionFactoryJNDI) {
            this.connectionFactoryJNDI = connectionFactoryJNDI;
            return (T) this;
        }

        public T destinationJNDI(String destinationJNDI) {
            this.destinationJNDI = destinationJNDI;
            return (T) this;
        }

        public T sessionAutoCommit(boolean sessionAutoCommit) {
            this.sessionAutoCommit = sessionAutoCommit;
            return (T) this;
        }

        public T autoStartListener(boolean autoStartListener) {
            this.autoStartListener = autoStartListener;
            return (T) this;
        }

        public T messageTracker(MessageTracker... messageTracker) {
            this.trackers.addAll(Arrays.asList(messageTracker));
            return (T) this;
        }

        public T messageTracker(Class<? extends MessageTracker>... messageTrackerClass) {
            List<MessageTracker> trackers = new ArrayList<>(messageTrackerClass.length);
            for (Class<? extends MessageTracker> cls : messageTrackerClass) {
                trackers.add(ReflectionUtils.instantiateClass(cls));
            }
            this.trackers.addAll(trackers);
            return (T) this;
        }

        public T messageListener(Class<? extends MessageListener> messageListenerClass) {
            if (!ClassFilterFeature.NEWABLE.accept(messageListenerClass)) {
                throw new IllegalArgumentException("can't create message listener " + messageListenerClass);
            }
            this.messageListenerClass = messageListenerClass;
            return (T) this;
        }

        public T messageListener(MessageListener messageListener) {
            this.messageListener = messageListener;
            return (T) this;
        }

        public MessageHelper build() {
            final ConnectionFactory connectionFactory;
            final Destination destination;
            if (this.connectionFactory == null && this.connectionFactoryJNDI == null) {
                throw new IllegalStateException("connection factory not set");
            }
            if (this.destination == null && this.destinationJNDI == null) {
                throw new IllegalStateException("destinaction not set");
            }
            if (this.connectionFactory == null) {
                try {
                    InitialContext context = new InitialContext(contextProperties);
                    connectionFactory = (ConnectionFactory) context.lookup(connectionFactoryJNDI);
                } catch (NamingException e) {
                    throw new IllegalStateException(connectionFactoryJNDI + " connection factory not found", e);
                }
            } else {
                connectionFactory = this.connectionFactory;
            }
            if (this.destination == null) {
                try {
                    InitialContext context = new InitialContext(contextProperties);
                    destination = (Destination) context.lookup(destinationJNDI);
                } catch (NamingException e) {
                    throw new IllegalStateException(destinationJNDI + " destination not found", e);
                }
            } else {
                destination = this.destination;
            }

            MessageHelper helper = new MessageHelper();
            helper.connectionFactory = connectionFactory;
            helper.destination = destination;
            helper.acknowledgeMode = acknowledgeMode;
            helper.transacted = transacted;
            helper.username = this.username;
            helper.password = this.password;
            helper.sessionAutoCommit = this.sessionAutoCommit;
            helper.autoStartListener = this.autoStartListener;
            helper.trackers = new MessageTrackers(this.trackers);
            final MessageListener listener = (messageListener == null && messageListenerClass != null)
                    ? ReflectionUtils.instantiateClass(this.messageListenerClass) : messageListener;
            if (listener != null) {
                helper.setListener(listener);
            }
            return helper;
        }

    }

    /**
     * 消息创建外部接口
     * 
     * @author wuxii@foxmail.com
     */
    public interface MessageConfiger extends Serializable {

        Message message(Session session) throws JMSException;

    }

    /**
     * 消息中需要而外属性时候的扩展接口
     * 
     * @author wuxii@foxmail.com
     */
    public interface MessageAppender<T extends Message> extends Serializable {

        void append(T message);

    }
}

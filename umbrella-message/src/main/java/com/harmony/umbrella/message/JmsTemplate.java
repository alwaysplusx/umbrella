package com.harmony.umbrella.message;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import com.harmony.umbrella.util.StringUtils;

public class JmsTemplate {

    ConnectionFactory connectionFactory;
    Destination destination;

    private final AtomicInteger sessionPointId = new AtomicInteger();

    private final Map<Integer, SessionPoint> sessions = new Hashtable<>();

    // setup
    protected String username;
    protected String password;
    protected boolean transacted = true;
    protected int sessionMode = Session.AUTO_ACKNOWLEDGE;
    protected boolean sessionAutoCommit;
    protected ExceptionListener exceptionListener;

    // consume
    protected String messageSelector;

    // shared connection
    private Connection connection;

    public JmsTemplate(ConnectionFactory connectionFactory, Destination destination) {
        this.connectionFactory = connectionFactory;
        this.destination = destination;
    }

    JmsTemplate() {
    }

    /**
     * 启动jmsTemplate
     * 
     * @throws JMSException
     */
    public void start() {
        if (connection != null) {
            throw new MessageException("the connection not close yet");
        }
        try {
            if (StringUtils.isNotBlank(username)) {
                this.connection = connectionFactory.createConnection(username, password);
            } else {
                this.connection = connectionFactory.createConnection();
            }
            if (exceptionListener != null) {
                this.connection.setExceptionListener(exceptionListener);
            }
            this.connection.start();
        } catch (JMSException e) {
            throw new MessageException("jms template start failed", e);
        }
    }

    /**
     * 关闭jmsTemplate
     * 
     * @throws JMSException
     */
    public void stop() {
        if (connection != null) {
            try {
                for (SessionPoint session : sessions.values()) {
                    if (sessionAutoCommit) {
                        session.commit();
                    }
                }
                sessions.clear();
                connection.close();
            } catch (Throwable e) {
                // ignore
            }
            connection = null;
        }
    }

    public SessionPoint newSession() {
        return newSession(destination, messageSelector);
    }

    public SessionPoint newSession(String messageSelector) {
        return newSession(destination, messageSelector);
    }

    public SessionPoint newSession(Destination destination, String messageSelector) {
        if (connection == null) {
            start();
        }
        try {
            Session session = connection.createSession(transacted, sessionMode);
            SessionPoint sessionPoint = new SessionPoint(session, destination, messageSelector);
            sessions.put(sessionPoint.getId(), sessionPoint);
            return sessionPoint;
        } catch (JMSException e) {
            throw new MessageException("new jms template sesion failed", e);
        }
    }

    // jms resources

    /**
     * 获取jms资源中的connection factory
     * 
     * @return connection factory
     */
    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    /**
     * 获取jms资源中的destination
     * 
     * @return destination
     */
    public Destination getDestination() {
        return destination;
    }

    // configuration

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isTransacted() {
        return transacted;
    }

    public int getSessionMode() {
        return sessionMode;
    }

    public boolean isSessionAutoCommit() {
        return sessionAutoCommit;
    }

    public ExceptionListener getExceptionListener() {
        return exceptionListener;
    }

    public String getMessageSelector() {
        return messageSelector;
    }

    void setUsername(String username) {
        this.username = username;
    }

    void setPassword(String password) {
        this.password = password;
    }

    void setTransacted(boolean transacted) {
        this.transacted = transacted;
    }

    void setSessionMode(int sessionMode) {
        this.sessionMode = sessionMode;
    }

    void setSessionAutoCommit(boolean sessionAutoCommit) {
        this.sessionAutoCommit = sessionAutoCommit;
    }

    void setExceptionListener(ExceptionListener exceptionListener) {
        this.exceptionListener = exceptionListener;
    }

    void setMessageSelector(String messageSelector) {
        this.messageSelector = messageSelector;
    }

    public final class SessionPoint {

        private int id;
        private Destination destination;
        private Session session;
        private MessageProducer messageProducer;
        private MessageConsumer messageConsumer;
        private String messageSelector;

        public SessionPoint(Session session, Destination destination) {
            this(session, destination, null);
        }

        public SessionPoint(Session session, Destination destination, String messageSelector) {
            this.id = sessionPointId.getAndIncrement();
            this.session = session;
            this.destination = destination;
            this.messageSelector = messageSelector;
        }

        public int getId() {
            return id;
        }

        public String getDestinationName() {
            try {
                if (destination instanceof Queue) {
                    return ((Queue) destination).getQueueName();
                } else if (destination instanceof Topic) {
                    return ((Topic) destination).getTopicName();
                }
            } catch (JMSException e) {
            }
            return destination.toString();
        }

        public Session getSession() {
            return session;
        }

        public Destination getDestination() {
            return destination;
        }

        public MessageProducer getMessageProducer() {
            if (messageProducer == null) {
                try {
                    messageProducer = getSession().createProducer(destination);
                } catch (JMSException e) {
                    throw new MessageException("create message producer failed", e);
                }
            }
            return messageProducer;
        }

        public MessageConsumer getMessageConsumer() {
            if (messageConsumer == null) {
                try {
                    messageConsumer = session.createConsumer(destination, messageSelector);
                } catch (JMSException e) {
                    throw new MessageException("create message consumer failed", e);
                }
            }
            return messageConsumer;
        }

        public void commit() {
            try {
                session.commit();
            } catch (JMSException e) {
                throw new MessageException("commit session failed", e);
            }
        }

        public void rollback() {
            try {
                session.rollback();
            } catch (JMSException e) {
                throw new MessageException("rollback session failed", e);
            }
        }

        public void release() {
            if (sessionAutoCommit) {
                commit();
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
            sessions.remove(id);
        }

    }

}

package com.harmony.umbrella.message.support;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import com.harmony.umbrella.message.JmsTemplate;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleJmsTemplate implements JmsTemplate {

    protected String username;
    protected String password;
    protected boolean transacted;
    protected int sessionMode;
    protected boolean sessionAutoCommit;
    protected ExceptionListener exceptionListener;

    protected ConnectionFactory connectionFactory;
    protected Destination destination;

    private Connection connection;
    private Session session;
    private MessageProducer messageProducer;
    private MessageConsumer messageConsumer;

    private boolean sessionRrollbacked;
    private boolean sessionCommited;

    public SimpleJmsTemplate() {
    }

    public SimpleJmsTemplate(ConnectionFactory connectionFactory, Destination destination) {
        this.connectionFactory = connectionFactory;
        this.destination = destination;
    }

    public SimpleJmsTemplate(ConnectionFactory connectionFactory, Destination destination, boolean sessionAutoCommit) {
        this.connectionFactory = connectionFactory;
        this.destination = destination;
        this.sessionAutoCommit = sessionAutoCommit;
    }

    @Override
    public boolean isStarted() {
        return connection != null;
    }

    @Override
    public void start() throws JMSException {
        if (StringUtils.isNotBlank(username)) {
            this.connection = connectionFactory.createConnection(username, password);
        } else {
            this.connection = connectionFactory.createConnection();
        }
        if (exceptionListener != null) {
            this.connection.setExceptionListener(exceptionListener);
        }
        this.connection.start();
    }

    @Override
    public void stop() throws JMSException {
        if (sessionAutoCommit && !sessionRrollbacked && !sessionCommited) {
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
            session = connection.createSession(transacted, sessionMode);
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

    @Override
    public JMSContext createJmsContext() {
        JMSContext ctx = null;
        if (username == null) {
            ctx = connectionFactory.createContext(username, password, sessionMode);
        } else {
            ctx = connectionFactory.createContext(sessionMode);
        }
        return ctx;
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

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public ExceptionListener getExceptionListener() {
        return exceptionListener;
    }

    public void setExceptionListener(ExceptionListener exceptionListener) {
        this.exceptionListener = exceptionListener;
    }

}

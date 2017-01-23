package com.harmony.umbrella.message;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

/**
 * jms配置的基础实现
 * 
 * @author wuxii@foxmail.com
 */
public class SimpleJmsTemplate implements JmsTemplate {

    protected boolean sessionAutoCommit;

    protected ConnectionFactory connectionFactory;
    protected Destination destination;

    private boolean sessionRrollbacked;
    private boolean sessionCommited;

    private Connection connection;
    private Session session;
    private MessageProducer messageProducer;
    private MessageConsumer messageConsumer;

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
    public boolean isStated() {
        return connection != null;
    }

    @Override
    public void start() throws JMSException {
        // stop/clear first
        stop();
        this.connection = connectionFactory.createConnection();
        this.connection.start();
    }

    @Override
    public Connection getConnection() throws JMSException {
        if (connection == null) {
            throw new IllegalStateException("connection not start");
        }
        return connection;
    }

    @Override
    public Session getSession() throws JMSException {
        if (session == null) {
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
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
    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    @Override
    public Destination getDestination() {
        return destination;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public boolean isSessionAutoCommit() {
        return sessionAutoCommit;
    }

    public void setSessionAutoCommit(boolean sessionAutoCommit) {
        this.sessionAutoCommit = sessionAutoCommit;
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

package com.harmony.umbrella.message;

import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.harmony.umbrella.util.ClassFilterFeature;

/**
 * 
 * @author wuxii@foxmail.com
 */
public class MessageTemplateBuilder<T extends MessageTemplateBuilder<T>> {

    protected ConnectionFactory connectionFactory;
    protected Destination destination;

    protected String username;
    protected String password;
    protected boolean transacted = true;
    protected int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;
    protected boolean sessionAutoCommit = true;
    protected boolean autoStartListener = true;

    protected Class<? extends MessageListener> messageListenerClass;
    protected MessageListener messageListener;
    protected MessageEventListener messageEventListener;

    protected String connectionFactoryJNDI;
    protected String destinationJNDI;
    protected Properties contextProperties = new Properties();

    public static MessageTemplateBuilder newBuilder(ConnectionFactory connectionFactory, Destination destination) {
        return new MessageTemplateBuilder<>(connectionFactory, destination);
    }

    public static MessageTemplateBuilder newBuilder() {
        return new MessageTemplateBuilder<>();
    }

    public MessageTemplateBuilder() {
    }

    public MessageTemplateBuilder(ConnectionFactory connectionFactory, Destination destination) {
        this.connectionFactory = connectionFactory;
        this.destination = destination;
    }

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

    public MessageTemplate build() {
        final ConnectionFactory connectionFactory = getConnectionFactory();
        final Destination destination = getDestination();

        MessageHelper helper = new MessageHelper();
        helper.connectionFactory = connectionFactory;
        helper.destination = destination;
        helper.acknowledgeMode = acknowledgeMode;
        helper.transacted = transacted;
        helper.username = this.username;
        helper.password = this.password;
        helper.sessionAutoCommit = this.sessionAutoCommit;
        helper.autoStartListener = this.autoStartListener;
        helper.messageEventListener = this.messageEventListener;

        MessageListener listener = getMessageListener();
        if (listener != null) {
            helper.setMessageListener(listener);
        }

        return helper;
    }

    protected Destination getDestination() {
        if (this.destination != null) {
            return destination;
        }
        if (this.destinationJNDI == null) {
            throw new IllegalStateException("destinaction not set");
        }
        Object dest = lookup(destinationJNDI);
        if (!(dest instanceof Destination)) {
            throw new IllegalStateException(dest + " not type of destination");
        }
        return (Destination) dest;
    }

    protected ConnectionFactory getConnectionFactory() {
        if (connectionFactory != null) {
            return connectionFactory;
        }
        if (connectionFactoryJNDI == null) {
            throw new IllegalStateException("connection factory not set");
        }
        Object cf = lookup(connectionFactoryJNDI);
        if (!(cf instanceof ConnectionFactory)) {
            throw new IllegalStateException(cf + " not type of connection factory");
        }
        return (ConnectionFactory) cf;
    }

    protected MessageListener getMessageListener() {
        MessageListener listener = this.messageListener;
        if (listener == null && messageListenerClass != null) {
            try {
                listener = messageListenerClass.newInstance();
            } catch (Exception e) {
                throw new IllegalStateException("unable create message listener instance", e);
            }
        }
        return listener;
    }

    protected Object lookup(String jndi) {
        try {
            return new InitialContext(contextProperties).lookup(jndi);
        } catch (NamingException e) {
            throw new IllegalStateException(jndi + " not found, ", e);
        }
    }

}
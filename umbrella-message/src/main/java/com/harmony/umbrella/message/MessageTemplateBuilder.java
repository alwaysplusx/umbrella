package com.harmony.umbrella.message;

import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
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
    protected int sessionMode = Session.AUTO_ACKNOWLEDGE;
    protected boolean sessionAutoCommit = true;
    protected boolean autoStartListener = true;

    protected Class<? extends ExceptionListener> exceptionListenerClass;
    protected ExceptionListener exceptionListener;

    protected Class<? extends MessageListener> messageListenerClass;
    protected MessageListener messageListener;

    protected Class<? extends MessageMonitor> messageMonitorClass;
    protected MessageMonitor messageMonitor;

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

    public T setUsername(String username) {
        this.username = username;
        return (T) this;
    }

    public T setPassword(String password) {
        this.password = password;
        return (T) this;
    }

    public T setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        return (T) this;
    }

    public T setDestination(Destination destination) {
        this.destination = destination;
        return (T) this;
    }

    public T setTransacted(boolean transacted) {
        this.transacted = transacted;
        return (T) this;
    }

    public T setSessionMode(int sessionMode) {
        this.sessionMode = sessionMode;
        return (T) this;
    }

    public T addContextProperties(String name, String value) {
        contextProperties.setProperty(name, value);
        return (T) this;
    }

    public T setContextProperties(Properties properties) {
        contextProperties.putAll(properties);
        return (T) this;
    }

    public T setConnectionFactoryJNDI(String connectionFactoryJNDI) {
        this.connectionFactoryJNDI = connectionFactoryJNDI;
        return (T) this;
    }

    public T setDestinationJNDI(String destinationJNDI) {
        this.destinationJNDI = destinationJNDI;
        return (T) this;
    }

    public T setSessionAutoCommit(boolean sessionAutoCommit) {
        this.sessionAutoCommit = sessionAutoCommit;
        return (T) this;
    }

    public T setAutoStartListener(boolean autoStartListener) {
        this.autoStartListener = autoStartListener;
        return (T) this;
    }

    public T setMessageListener(Class<? extends MessageListener> messageListenerClass) {
        if (!ClassFilterFeature.NEWABLE.accept(messageListenerClass)) {
            throw new IllegalArgumentException("can't create message listener " + messageListenerClass);
        }
        this.messageListenerClass = messageListenerClass;
        return (T) this;
    }

    public T setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
        return (T) this;
    }

    public T setExceptionListener(Class<? extends ExceptionListener> listenerClass) {
        if (!ClassFilterFeature.NEWABLE.accept(listenerClass)) {
            throw new IllegalArgumentException("can't create exception listener " + listenerClass);
        }
        this.exceptionListenerClass = listenerClass;
        return (T) this;
    }

    public T setExceptionListener(ExceptionListener listener) {
        this.exceptionListener = listener;
        return (T) this;
    }

    public T setMessageMonitorClass(Class<? extends MessageMonitor> monitorClass) {
        if (!ClassFilterFeature.NEWABLE.accept(monitorClass)) {
            throw new IllegalArgumentException("can't create message monitor " + monitorClass);
        }
        this.messageMonitorClass = monitorClass;
        return (T) this;
    }

    public T setMessageMonitor(MessageMonitor monitor) {
        this.messageMonitor = monitor;
        return (T) this;
    }

    public MessageTemplate build() {
        final ConnectionFactory connectionFactory = getConnectionFactory();
        final Destination destination = getDestination();

        MessageHelper helper = new MessageHelper();
        helper.connectionFactory = connectionFactory;
        helper.destination = destination;
        helper.sessionMode = sessionMode;
        helper.transacted = transacted;
        helper.username = this.username;
        helper.password = this.password;
        helper.sessionAutoCommit = this.sessionAutoCommit;
        helper.autoStartListener = this.autoStartListener;
        helper.messageMonitor = this.messageMonitor;
        helper.exceptionListener = getExceptionMessageListener();

        MessageListener listener = getMessageListener();
        if (listener != null) {
            helper.setMessageListener(listener);
        }

        return helper;
    }

    // protected methods

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
            } catch (Throwable e) {
                throw new IllegalArgumentException("message listener create failed, " + messageListenerClass, e);
            }
        }
        return listener;
    }

    protected ExceptionListener getExceptionMessageListener() {
        ExceptionListener listener = this.exceptionListener;
        if (listener == null && exceptionListenerClass != null) {
            try {
                listener = exceptionListenerClass.newInstance();
            } catch (Throwable e) {
                throw new IllegalArgumentException("exception listener create failed, " + exceptionListenerClass, e);
            }
        }
        return listener;
    }

    protected MessageMonitor getMessageMonitor() {
        MessageMonitor monitor = this.messageMonitor;
        if (monitor == null && messageMonitorClass != null) {
            try {
                monitor = messageMonitorClass.newInstance();
            } catch (Throwable e) {
                throw new IllegalArgumentException("message monitor create failed, " + messageMonitorClass, e);
            }
        }
        return monitor;
    }

    protected Object lookup(String jndi) {
        try {
            return new InitialContext(contextProperties).lookup(jndi);
        } catch (NamingException e) {
            throw new IllegalStateException(jndi + " not found, ", e);
        }
    }

}
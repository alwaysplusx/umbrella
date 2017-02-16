package com.harmony.umbrella.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    protected List<MessageTracker> trackers = new ArrayList<>();

    protected Class<? extends MessageListener> messageListenerClass;
    protected MessageListener messageListener;

    protected String connectionFactoryJNDI;
    protected String destinationJNDI;
    protected Properties contextProperties = new Properties();

    public static MessageTemplateBuilder createBuilder(ConnectionFactory connectionFactory, Destination destination) {
        return new MessageTemplateBuilder<>(connectionFactory, destination);
    }

    public static MessageTemplateBuilder createBuilder() {
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

    public T messageTracker(MessageTracker... messageTracker) {
        this.trackers.addAll(Arrays.asList(messageTracker));
        return (T) this;
    }

    public T messageTracker(Class<? extends MessageTracker>... messageTrackerClass) {
        List<MessageTracker> trackers = new ArrayList<>(messageTrackerClass.length);
        for (Class<? extends MessageTracker> cls : messageTrackerClass) {
            try {
                trackers.add(cls.newInstance());
            } catch (Exception e) {
                throw new IllegalArgumentException("can't create instance of " + cls);
            }
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

    public MessageTemplate build() {
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
        MessageListener listener;
        try {
            listener = (messageListener == null && messageListenerClass != null) ? this.messageListenerClass.newInstance() : messageListener;
            if (listener != null) {
                helper.setMessageListener(listener);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("can't create message listense instance " + this.messageListenerClass);
        }
        return helper;
    }

}
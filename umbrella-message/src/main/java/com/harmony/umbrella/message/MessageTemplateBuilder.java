package com.harmony.umbrella.message;

import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.harmony.umbrella.message.MessageTemplateFactory.TemplateConfig;

/**
 * 
 * @author wuxii@foxmail.com
 */
public class MessageTemplateBuilder<T extends MessageTemplateBuilder<T>> {

    private JmsTemplate jmsTemplate;

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

    protected MessageTemplateBuilder() {
        this.jmsTemplate = new JmsTemplate();
    }

    protected MessageTemplateBuilder(ConnectionFactory connectionFactory, Destination destination) {
        this.jmsTemplate = new JmsTemplate(connectionFactory, destination);
    }

    public T setUsername(String username) {
        this.jmsTemplate.setUsername(username);
        return (T) this;
    }

    public T setPassword(String password) {
        this.jmsTemplate.setPassword(password);
        return (T) this;
    }

    public T setConnectionFactory(ConnectionFactory connectionFactory) {
        this.jmsTemplate.connectionFactory = connectionFactory;
        return (T) this;
    }

    public T setDestination(Destination destination) {
        this.jmsTemplate.destination = destination;
        return (T) this;
    }

    public T setTransacted(boolean transacted) {
        this.jmsTemplate.setTransacted(transacted);
        return (T) this;
    }

    public T setSessionMode(int sessionMode) {
        this.jmsTemplate.setSessionMode(sessionMode);
        return (T) this;
    }

    public T setSessionAutoCommit(boolean sessionAutoCommit) {
        this.jmsTemplate.setSessionAutoCommit(sessionAutoCommit);
        return (T) this;
    }

    public T setExceptionListener(ExceptionListener listener) {
        this.jmsTemplate.setExceptionListener(listener);
        return (T) this;
    }

    public T setMessageSelector(String messageSelector) {
        this.jmsTemplate.setMessageSelector(messageSelector);
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

    public T setExceptionListenerClass(Class<? extends ExceptionListener> listenerClass) {
        try {
            setExceptionListener(listenerClass.newInstance());
        } catch (Throwable e) {
            throw new IllegalArgumentException("can't create exception listener " + listenerClass);
        }
        return (T) this;
    }

    public T setMessageMonitorClass(Class<? extends MessageMonitor> monitorClass) {
        try {
            setMessageMonitor(monitorClass.newInstance());
        } catch (Throwable e) {
            throw new IllegalArgumentException("can't create message monitor " + monitorClass);
        }
        return (T) this;
    }

    public T setMessageMonitor(MessageMonitor monitor) {
        this.messageMonitor = monitor;
        return (T) this;
    }

    public T apply(TemplateConfig cfg) {
        return apply(cfg, true);
    }

    public T applyIfAbsent(TemplateConfig cfg) {
        return apply(cfg, false);
    }

    protected T apply(TemplateConfig cfg, boolean replace) {
        JmsTemplate o = this.jmsTemplate;
        // apply to jms template
        if (replace || o.username == null) {
            o.username = cfg.getUsername();
        }
        if (replace || o.password == null) {
            o.password = cfg.getPassword();
        }
        if (replace || o.transacted != cfg.isTransacted()) {
            o.transacted = cfg.isTransacted();
        }
        if (replace || o.sessionAutoCommit != cfg.isSessionAutoCommit()) {
            o.sessionAutoCommit = cfg.isSessionAutoCommit();
        }
        if (replace || o.exceptionListener == null) {
            o.exceptionListener = cfg.getExceptionListener();
        }
        if (replace || messageMonitor == null) {
            this.messageMonitor = cfg.getMessageMonitor();
        }
        if (replace || (connectionFactoryJNDI == null && o.connectionFactory == null)) {
            o.connectionFactory = cfg.getConnectionFactory();
            connectionFactoryJNDI = null;
        }
        if (replace || (destinationJNDI == null && o.destination == null)) {
            o.destination = cfg.getDestination();
            this.destinationJNDI = null;
        }
        return (T) this;
    }

    public MessageTemplate build() {
        JmsTemplate jmsTemplate = buildJmsTemplate();
        MessageHelper helper = new MessageHelper(jmsTemplate);
        helper.setMessageMonitor(messageMonitor);
        return helper;
    }

    public JmsTemplate buildJmsTemplate() {
        JmsTemplate o = new JmsTemplate();
        o.connectionFactory = getConnectionFactory();
        o.destination = getDestination();
        o.username = jmsTemplate.username;
        o.password = jmsTemplate.password;
        o.transacted = jmsTemplate.transacted;
        o.sessionMode = jmsTemplate.sessionMode;
        o.sessionAutoCommit = jmsTemplate.sessionAutoCommit;
        o.exceptionListener = jmsTemplate.exceptionListener;
        return o;
    }

    // protected methods

    protected Destination getDestination() {
        if (this.jmsTemplate.destination != null) {
            return this.jmsTemplate.destination;
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
        if (this.jmsTemplate.connectionFactory != null) {
            return this.jmsTemplate.connectionFactory;
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

    protected Object lookup(String jndi) {
        try {
            return new InitialContext(contextProperties).lookup(jndi);
        } catch (NamingException e) {
            throw new IllegalStateException(jndi + " not found, ", e);
        }
    }

}
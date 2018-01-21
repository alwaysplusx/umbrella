package com.harmony.umbrella.autoconfigure.message;

import java.util.List;

import javax.jms.Session;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wuxii@foxmail.com
 */
@ConfigurationProperties(prefix = "harmony.message")
public class MessageProperties {

    private BrokerConfig broker;

    private TemplateConfig queueTemplate;

    private TemplateConfig topicTemplate;

    public BrokerConfig getBroker() {
        return broker;
    }

    public void setBroker(BrokerConfig broker) {
        this.broker = broker;
    }

    public TemplateConfig getQueueTemplate() {
        return queueTemplate;
    }

    public void setQueueTemplate(TemplateConfig queueTemplate) {
        this.queueTemplate = queueTemplate;
    }

    public TemplateConfig getTopicTemplate() {
        return topicTemplate;
    }

    public void setTopicTemplate(TemplateConfig topicTemplate) {
        this.topicTemplate = topicTemplate;
    }

    public static class ListenerConfig {

        private String messageSelector;

        public String getMessageSelector() {
            return messageSelector;
        }

        public void setMessageSelector(String messageSelector) {
            this.messageSelector = messageSelector;
        }

    }

    public static class BrokerConfig {

        String connector;
        boolean ssl;
        boolean persistent;
        String tmpDataDirectory;
        String dataDirectory;

        public String getConnector() {
            return connector;
        }

        public void setConnector(String connector) {
            this.connector = connector;
        }

        public boolean isSsl() {
            return ssl;
        }

        public void setSsl(boolean ssl) {
            this.ssl = ssl;
        }

        public boolean isPersistent() {
            return persistent;
        }

        public void setPersistent(boolean persistent) {
            this.persistent = persistent;
        }

        public String getTmpDataDirectory() {
            return tmpDataDirectory;
        }

        public void setTmpDataDirectory(String tmpDataDirectory) {
            this.tmpDataDirectory = tmpDataDirectory;
        }

        public String getDataDirectory() {
            return dataDirectory;
        }

        public void setDataDirectory(String dataDirectory) {
            this.dataDirectory = dataDirectory;
        }
    }

    public static class TemplateConfig {

        private String username;
        private String password;
        private boolean transacted = true;
        private int sessionMode = Session.AUTO_ACKNOWLEDGE;
        private boolean sessionAutoCommit = true;
        private String connector;
        private String destination;

        private List<ListenerConfig> listeners;

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

        public String getConnector() {
            return connector;
        }

        public void setConnector(String connector) {
            this.connector = connector;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public List<ListenerConfig> getListeners() {
            return listeners;
        }

        public void setListeners(List<ListenerConfig> listeners) {
            this.listeners = listeners;
        }

    }

}

package com.harmony.umbrella.autoconfigure.message;

import java.util.List;

import javax.jms.Session;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wuxii@foxmail.com
 */
@ConfigurationProperties(prefix = "harmony.message")
public class MessageProperties {

    private List<MessageConfig> templates;

    public List<MessageConfig> getTemplates() {
        return templates;
    }

    public void setTemplates(List<MessageConfig> templates) {
        this.templates = templates;
    }

    public static class MessageConfig {

        private String username;
        private String password;
        private boolean transacted = true;
        private int sessionMode = Session.AUTO_ACKNOWLEDGE;
        private boolean sessionAutoCommit = true;
        private boolean autoStartListener = true;
        private String connector;
        private String destination;

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

        public boolean isAutoStartListener() {
            return autoStartListener;
        }

        public void setAutoStartListener(boolean autoStartListener) {
            this.autoStartListener = autoStartListener;
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

    }

}

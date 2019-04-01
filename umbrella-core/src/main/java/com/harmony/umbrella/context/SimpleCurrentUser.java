package com.harmony.umbrella.context;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author wuxii
 */
public class SimpleCurrentUser implements CurrentUser {

    private final Long userId;
    private final String username;
    private final Map<Object, Object> userProperties;

    private SimpleCurrentUser(Long userId, String username, Map<Object, Object> userProperties) {
        this.userId = userId;
        this.username = username;
        this.userProperties = Collections.unmodifiableMap(userProperties);
    }

    @Override
    public long getUserId() {
        return userId;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Map<Object, Object> getUserProperties() {
        return userProperties;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private long userId = 0;
        private String username;
        private Map<Object, Object> userProperties = new LinkedHashMap<>();

        private Builder() {
        }

        public Builder setUserId(long userId) {
            this.userId = userId;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder addProperty(Object key, Object value) {
            userProperties.put(key, value);
            return this;
        }

        public Builder addProperties(Map<Object, Object> properties) {
            userProperties.putAll(properties);
            return this;
        }

        public CurrentUser build() {
            return new SimpleCurrentUser(userId, username, userProperties);
        }

    }

}

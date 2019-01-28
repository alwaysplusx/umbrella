package com.harmony.umbrella.context;

import java.util.Collections;
import java.util.Map;

/**
 * @author wuxii
 */
public final class CurrentUser {

    private final Long userId;
    private final String username;
    private final Map<String, Object> userProperties;

    public CurrentUser(Long userId, String username, Map<String, Object> userProperties) {
        this.userId = userId;
        this.username = username;
        this.userProperties = Collections.unmodifiableMap(userProperties);
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public Map<String, Object> getUserProperties() {
        return userProperties;
    }

    public Object getUserProperty(String name) {
        return userProperties.get(name);
    }

}

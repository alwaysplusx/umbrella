package com.harmony.umbrella.context;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

/**
 * @author wuxii
 */
public interface CurrentUser extends Principal {

    long getUserId();

    default boolean isAnonymous() {
        return getUserId() <= 0;
    }

    String getUsername();

    @Override
    default String getName() {
        return getUsername();
    }

    default Object getUserProperty(Object key) {
        return getUserProperties().get(key);
    }

    Map<Object, Object> getUserProperties();

    static CurrentUser anonymous() {
        return ANONYMOUS;
    }

    CurrentUser ANONYMOUS = new CurrentUser() {

        @Override
        public long getUserId() {
            return 0L;
        }

        @Override
        public String getUsername() {
            return "anonymous";
        }

        @Override
        public Map<Object, Object> getUserProperties() {
            return Collections.emptyMap();
        }
    };

}

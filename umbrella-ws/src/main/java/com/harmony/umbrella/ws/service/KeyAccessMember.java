package com.harmony.umbrella.ws.service;

import java.lang.reflect.Method;

import com.harmony.umbrella.access.AccessMember;
import com.harmony.umbrella.ws.annotation.Key;

/**
 * @author wuxii@foxmail.com
 */
public final class KeyAccessMember extends AccessMember {

    // FIXME key annotation
    final Key key;

    public KeyAccessMember(Class<?> targetClass, Method readMethod, Key key) {
        super(targetClass, readMethod.getName());
        this.key = key;
    }

    public int getOrdinal() {
        return key.ordinal();
    }

    public String getKeyName() {
        return key.name();
    }

}
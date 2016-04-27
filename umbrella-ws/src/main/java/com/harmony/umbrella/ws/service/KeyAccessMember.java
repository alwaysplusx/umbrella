package com.harmony.umbrella.ws.service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.harmony.umbrella.core.AccessMember;
import com.harmony.umbrella.ws.annotation.Key;

/**
 * @author wuxii@foxmail.com
 */
public final class KeyAccessMember extends AccessMember {

    final Key key;

    public KeyAccessMember(Class<?> targetClass, Method readMethod, Key key) {
        super(targetClass, readMethod);
        this.key = key;
    }

    public KeyAccessMember(Class<?> targetClass, Field field, Key key) {
        super(targetClass, field);
        this.key = key;
    }

    public int getOrdinal() {
        return key.ordinal();
    }

    public String getKeyName() {
        return key.name();
    }

}
package com.harmony.umbrella.context.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.harmony.umbrella.context.CurrentContext;

/**
 * @author wuxii@foxmail.com
 */
public class SimplePrincipals implements CurrentContext.Principals {

    private Map<Object, Object> principals = new LinkedHashMap<>();

    @Override
    public List<Object> getPrincipalKeys() {
        return new ArrayList<>(principals.keySet());
    }

    @Override
    public Iterator<Object> iterator() {
        return principals.values().iterator();
    }

    @Override
    public <T> T getPrincipal(Class<T> type) {
        return (T) principals.get(type);
    }

    @Override
    public Object getPrincipal(String name) {
        return principals.get(name);
    }

    public String getUsername() {
        return (String) getPrincipal("username");
    }

    public String getClientId() {
        return (String) getPrincipal("clientId");
    }

    public void add(String name, Object principal) {
        principals.put(name, principal);
    }

    public void add(Object principal) {
        principals.put(principal.getClass(), principal);
    }

}

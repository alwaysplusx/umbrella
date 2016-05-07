package com.harmony.umbrella.plugin.ws;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Map;

import com.harmony.umbrella.ws.Context;
import com.harmony.umbrella.ws.Metadata;

/**
 * @author wuxii@foxmail.com
 */
public class ContextWrapper implements Context {

    private static final long serialVersionUID = 6312523048782417988L;

    private Context context;
    private Metadata metadata;

    public ContextWrapper(Context context) {
        this(context, context);
    }

    public ContextWrapper(Context context, Metadata metadata) {
        this.context = context;
        this.metadata = metadata;
    }

    @Override
    public String getServiceName() {
        return context.getServiceName();
    }

    @Override
    public Class<?> getServiceClass() {
        return context.getServiceClass();
    }

    @Override
    public long getConnectionTimeout() {
        return metadata.getConnectionTimeout();
    }

    @Override
    public long getReceiveTimeout() {
        return metadata.getReceiveTimeout();
    }

    @Override
    public int getSynchronousTimeout() {
        return metadata.getSynchronousTimeout();
    }

    @Override
    public long getContextId() {
        return context.getContextId();
    }

    @Override
    public Class<?> getServiceInterface() {
        return context.getServiceInterface();
    }

    @Override
    public Object[] getParameters() {
        return context.getParameters();
    }

    @Override
    public String getMethodName() {
        return context.getMethodName();
    }

    @Override
    public String getAddress() {
        return metadata.getAddress();
    }

    @Override
    public String getUsername() {
        return metadata.getUsername();
    }

    @Override
    public String getPassword() {
        return metadata.getPassword();
    }

    @Override
    public Object get(String contextKey) {
        return context.get(contextKey);
    }

    @Override
    public boolean contains(String key) {
        return context.contains(key);
    }

    @Override
    public Method getMethod() throws NoSuchMethodException {
        return context.getMethod();
    }

    @Override
    public Enumeration<String> getContextNames() {
        return context.getContextNames();
    }

    @Override
    public Map<String, Object> getContextMap() {
        return context.getContextMap();
    }

    @Override
    public void put(String key, Object value) {
        context.put(key, value);
    }

}

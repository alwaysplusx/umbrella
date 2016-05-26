package com.harmony.umbrella.context.ee.support;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.harmony.umbrella.context.ee.ContextFactory;

/**
 * @author wuxii@foxmail.com
 */
public class ContextFactoryImpl implements ContextFactory {

    private final Properties properties = new Properties();

    public ContextFactoryImpl() {
    }

    public ContextFactoryImpl(Properties properties) {
        this.properties.putAll(properties);
    }

    @Override
    public Context getContext() throws NamingException {
        return getContext(properties);
    }

    @Override
    public Context getContext(Properties properties) throws NamingException {
        return new InitialContext(properties);
    }

    @Override
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties.clear();
        this.properties.putAll(properties);
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

}

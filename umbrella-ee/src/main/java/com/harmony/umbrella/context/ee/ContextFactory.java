package com.harmony.umbrella.context.ee;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;

/**
 * @author wuxii@foxmail.com
 */
public interface ContextFactory {

    Context getContext() throws NamingException;

    Context getContext(Properties properties) throws NamingException;

    void setProperty(String key, String value);

    void setProperties(Properties properties);

    Properties getProperties();

}

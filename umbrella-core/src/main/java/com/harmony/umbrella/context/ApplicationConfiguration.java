package com.harmony.umbrella.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import com.harmony.umbrella.context.ApplicationContext.ApplicationInitializer;
import com.harmony.umbrella.sql.ConnectionSource;

/**
 * @author wuxii@foxmail.com
 */
public class ApplicationConfiguration {

    protected Class<? extends ApplicationInitializer> applicationInitializerClass;

    protected ServletContext servletContext;

    protected final List<String> packages = new ArrayList<String>();

    protected final List<ConnectionSource> connectionSources = new ArrayList<ConnectionSource>();

    protected final Map<String, Object> properties = new HashMap<String, Object>();

    public ApplicationConfiguration withServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
        return this;
    }

    public ApplicationConfiguration withPackage(String... pkg) {
        Collections.addAll(packages, pkg);
        return this;
    }

    public ApplicationConfiguration withConnectionSource(ConnectionSource... connectionSource) {
        Collections.addAll(connectionSources, connectionSource);
        return this;
    }

    public ApplicationConfiguration withProperty(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }

    public ApplicationConfiguration withApplicationInitializerClass(Class<? extends ApplicationInitializer> applicationInitializerClass) {
        this.applicationInitializerClass = applicationInitializerClass;
        return this;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public List<String> getPackages() {
        return packages;
    }

    public void setPackages(List<String> packages) {
        this.packages.clear();
        this.packages.addAll(packages);
    }

    public List<ConnectionSource> getConnectionSources() {
        return connectionSources;
    }

    public void setConnectionSources(List<ConnectionSource> connectionSources) {
        this.connectionSources.clear();
        this.connectionSources.addAll(connectionSources);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties.clear();
        this.properties.putAll(properties);
    }

    public Class<? extends ApplicationInitializer> getApplicationInitializerClass() {
        return applicationInitializerClass;
    }

    public void setApplicationInitializerClass(Class<? extends ApplicationInitializer> applicationInitializerClass) {
        this.applicationInitializerClass = applicationInitializerClass;
    }

}

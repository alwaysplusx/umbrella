package com.harmony.umbrella.autoconfigure.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.harmony.umbrella.context.ApplicationContext.ApplicationContextInitializer;

/**
 * @author wuxii@foxmail.com
 */
@ConfigurationProperties(prefix = "harmony.app")
public class AppProperties {

    private String name;
    private Class<? extends ApplicationContextInitializer> initializer;
    private List<String> datasources;
    private List<String> packages = Collections.singletonList("com.harmony");
    private List<Class<? extends Runnable>> shutdownHooks;
    private Properties properties;

    public AppProperties() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<? extends ApplicationContextInitializer> getInitializer() {
        return initializer;
    }

    public void setInitializer(Class<? extends ApplicationContextInitializer> initializer) {
        this.initializer = initializer;
    }

    public List<String> getDatasources() {
        return datasources;
    }

    public void setDatasources(List<String> datasources) {
        this.datasources = datasources;
    }

    public List<Class<? extends Runnable>> getShutdownHooks() {
        return shutdownHooks;
    }

    public void setShutdownHooks(List<Class<? extends Runnable>> shutdownHooks) {
        this.shutdownHooks = shutdownHooks;
    }

    public List<String> getPackages() {
        return packages;
    }

    public void setPackages(List<String> packages) {
        this.packages = packages;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

}

package com.harmony.umbrella.autoconfigure.web;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.harmony.umbrella.context.ApplicationContext.ApplicationContextInitializer;

/**
 * @author wuxii@foxmail.com
 */
@ConfigurationProperties(prefix = "harmony.cfg")
public class WebAppProperties {

    private Class<? extends ApplicationContextInitializer> initializer;
    private List<String> datasources;
    private List<String> scanPackages = Arrays.asList("com.harmony");
    private List<Class<? extends Runnable>> shutdownHooks;
    private Properties properties;

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

    public List<String> getScanPackages() {
        return scanPackages;
    }

    public void setScanPackages(List<String> scanPackages) {
        this.scanPackages = scanPackages;
    }

    public List<Class<? extends Runnable>> getShutdownHooks() {
        return shutdownHooks;
    }

    public void setShutdownHooks(List<Class<? extends Runnable>> shutdownHooks) {
        this.shutdownHooks = shutdownHooks;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

}

package com.harmony.umbrella.autoconfigure.web;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.harmony.umbrella.context.ApplicationContext.ApplicationContextInitializer;

/**
 * @author wuxii@foxmail.com
 */
@ConfigurationProperties(prefix = "harmony.web")
public class WebAppProperties {

    private Class<? extends ApplicationContextInitializer> initializer;
    private List<String> datasources = Arrays.asList("jdbc/harmony");
    private List<String> scanPackages = Arrays.asList("com.harmony");
    private List<Class<? extends Runnable>> shutdownHooks;
    private boolean showInfo = true;
    private boolean autowire = true;
    private boolean scanHandlersTypes = true;

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

    public boolean isShowInfo() {
        return showInfo;
    }

    public void setShowInfo(boolean showInfo) {
        this.showInfo = showInfo;
    }

    public boolean isAutowire() {
        return autowire;
    }

    public void setAutowire(boolean autowire) {
        this.autowire = autowire;
    }

    public boolean isScanHandlersTypes() {
        return scanHandlersTypes;
    }

    public void setScanHandlersTypes(boolean scanHandlersTypes) {
        this.scanHandlersTypes = scanHandlersTypes;
    }

}

package com.harmony.umbrella.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import com.harmony.umbrella.context.ApplicationContext.ApplicationContextInitializer;
import com.harmony.umbrella.core.ConnectionSource;

/**
 * 应用配置信息
 * 
 * @author wuxii@foxmail.com
 */
public class ApplicationConfiguration {

    protected static final Pattern PACKAGE_PATTERN = Pattern.compile("^[a-zA-Z]+[0-9a-zA-Z]*");

    protected ServletContext servletContext;

    protected final List<String> packages = new ArrayList<String>();

    protected final List<ConnectionSource> connectionSources = new ArrayList<ConnectionSource>();

    protected final Map properties = new HashMap();

    protected boolean devMode;

    protected Class<? extends ApplicationContextInitializer> applicationContextInitializerClass;

    protected boolean initializedClassWhenScan;

    /**
     * 设置配置属性
     * 
     * @param key
     *            配置属性key
     * @param value
     *            配置属性value
     * @return this
     */
    public ApplicationConfiguration withProperty(String key, Object value) {
        properties.put(key, value);
        return this;
    }

    /**
     * 设置配置属性
     * 
     * @param properties
     *            配置属性
     * @return this
     */
    public ApplicationConfiguration withProperty(Properties properties) {
        this.properties.putAll(properties);
        return this;
    }

    /**
     * 设置配置属性
     * 
     * @param properties
     *            配置属性
     * @return this
     */
    public ApplicationConfiguration withProperty(Map<String, Object> properties) {
        this.properties.putAll(properties);
        return this;
    }

    /**
     * 设置应用所需要扫描的包
     * 
     * @param packages
     *            待扫描的包
     * @return this
     */
    public ApplicationConfiguration withPackages(String... packages) {
        for (String p : packages) {
            if (isPackage(p)) {
                addPackage(p);
            }
        }
        return this;
    }

    /**
     * 设置应用所需要扫描的包
     * 
     * @param packages
     *            待扫描的包
     * @return this
     */
    public ApplicationConfiguration withPackages(List<String> packages) {
        for (String p : packages) {
            if (isPackage(p)) {
                addPackage(p);
            }
        }
        return this;
    }

    /**
     * 在加载包下的class时候是否积极的初始化类
     * 
     * @param initializedClassWhenForName
     *            是否积极初始化类信息
     * @return this
     * @see Class#forName(String, boolean, ClassLoader)
     */
    public ApplicationConfiguration withInitializedClassWhenScan(boolean initializedClassWhenScan) {
        this.initializedClassWhenScan = initializedClassWhenScan;
        return this;
    }

    /**
     * 添加应用所拥有的数据源
     * 
     * @param connectionSource
     *            数据源
     * @return this
     */
    public ApplicationConfiguration withConnectionSource(ConnectionSource... connectionSource) {
        Collections.addAll(this.connectionSources, connectionSource);
        return this;
    }

    /**
     * 添加应用的数据源
     * 
     * @param connectionSource
     *            数据源
     * @return this
     */
    public ApplicationConfiguration withConnectionSource(List<ConnectionSource> connectionSource) {
        this.connectionSources.addAll(connectionSource);
        return this;
    }

    /**
     * 设置启动配置的servletContext
     * 
     * @param servletContext
     *            web application context
     * @return this
     */
    public ApplicationConfiguration withServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
        return this;
    }

    /**
     * 设置应用上下文的初始化类
     * 
     * @param applicationContextInitializerClass
     *            应用上下文的初始化类
     * @return this
     */
    public ApplicationConfiguration withApplicationInitializerClass(Class<? extends ApplicationContextInitializer> applicationContextInitializerClass) {
        this.applicationContextInitializerClass = applicationContextInitializerClass;
        return this;
    }

    /**
     * 设置是否为开发模式
     * 
     * @param mode
     *            开发模式标识
     * @return this
     */
    public ApplicationConfiguration withDevMode(boolean mode) {
        this.devMode = mode;
        return this;
    }

    // getter

    public ServletContext getServletContext() {
        return servletContext;
    }

    public List<String> getPackages() {
        return Collections.unmodifiableList(packages);
    }

    public List<ConnectionSource> getConnectionSources() {
        return Collections.unmodifiableList(connectionSources);
    }

    public boolean isDevMode() {
        return devMode;
    }

    public Map getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public Class<? extends ApplicationContextInitializer> getApplicationContextInitializerClass() {
        return applicationContextInitializerClass;
    }

    public boolean isInitializedClassWhenScan() {
        return initializedClassWhenScan;
    }

    // internal method

    private void addPackage(String p) {
        Iterator<String> it = packages.iterator();
        while (it.hasNext()) {
            String token = it.next();
            if (p.startsWith(token)) {
                // 已经存在父层的包
                return;
            } else if (token.startsWith(p)) {
                // 输入的包是当前包的父层包
                it.remove();
            }
        }
        packages.add(p);
    }

    private boolean isPackage(String pkg) {
        if (pkg == null) {
            return false;
        }
        Package p = Package.getPackage(pkg);
        if (p != null) {
            return true;
        }
        for (String token : pkg.split(".")) {
            if (!PACKAGE_PATTERN.matcher(token).matches()) {
                return false;
            }
        }
        return true;
    }

    public static final ApplicationConfiguration unmodifiableApplicationConfig(ApplicationConfiguration cfg) {
        return new UnmodifiableApplicationConfiguration(cfg);
    }

    @SuppressWarnings("rawtypes")
    private static final class UnmodifiableApplicationConfiguration extends ApplicationConfiguration {

        private ApplicationConfiguration cfg;

        UnmodifiableApplicationConfiguration(ApplicationConfiguration applicationConfiguration) {
            this.cfg = applicationConfiguration;
        }

        @Override
        public ApplicationConfiguration withProperty(String key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ApplicationConfiguration withProperty(Properties properties) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ApplicationConfiguration withProperty(Map<String, Object> properties) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ApplicationConfiguration withPackages(String... packages) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ApplicationConfiguration withPackages(List<String> packages) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ApplicationConfiguration withInitializedClassWhenScan(boolean initializedClassWhenScan) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ApplicationConfiguration withConnectionSource(ConnectionSource... connectionSource) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ApplicationConfiguration withConnectionSource(List<ConnectionSource> connectionSource) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ApplicationConfiguration withServletContext(ServletContext servletContext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ApplicationConfiguration withApplicationInitializerClass(Class<? extends ApplicationContextInitializer> applicationContextInitializerClass) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ApplicationConfiguration withDevMode(boolean mode) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ServletContext getServletContext() {
            return cfg.getServletContext();
        }

        @Override
        public List<String> getPackages() {
            return cfg.getPackages();
        }

        @Override
        public List<ConnectionSource> getConnectionSources() {
            return cfg.getConnectionSources();
        }

        @Override
        public boolean isDevMode() {
            return cfg.isDevMode();
        }

        @Override
        public Map getProperties() {
            return cfg.getProperties();
        }

        @Override
        public Class<? extends ApplicationContextInitializer> getApplicationContextInitializerClass() {
            return cfg.getApplicationContextInitializerClass();
        }

        @Override
        public boolean isInitializedClassWhenScan() {
            return cfg.isInitializedClassWhenScan();
        }

    }
}

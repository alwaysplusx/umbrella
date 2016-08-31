package com.harmony.umbrella.context;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import com.harmony.umbrella.context.ApplicationContext.ApplicationInitializer;
import com.harmony.umbrella.context.metadata.DatabaseMetadata.ConnectionSource;

/**
 * @author wuxii@foxmail.com
 */
public class ApplicationConfiguration {

    private static final Pattern PACKAGE_PATTERN = Pattern.compile("^[a-zA-Z]+[0-9a-zA-Z]*");

    private ServletContext servletContext;
    private List<String> packages = new ArrayList<String>();
    private ConnectionSource connectionSource;

    private boolean devMode;

    @SuppressWarnings("rawtypes")
    private Map properties = new HashMap();

    private Class<? extends ApplicationInitializer> applicationInitializerClass;

    private boolean initializeClass;

    @SuppressWarnings("unchecked")
    public ApplicationConfiguration withProperty(String key, Object value) {
        properties.put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public ApplicationConfiguration withProperty(Properties properties) {
        this.properties.putAll(properties);
        return this;
    }

    @SuppressWarnings("unchecked")
    public ApplicationConfiguration withProperty(Map<String, Object> properties) {
        this.properties.putAll(properties);
        return this;
    }

    public ApplicationConfiguration withPackages(String... packages) {
        for (String p : packages) {
            if (isPackage(p)) {
                addPackage(p);
            }
        }
        return this;
    }

    public ApplicationConfiguration withInitializeClass(boolean initialize) {
        this.initializeClass = initialize;
        return this;
    }

    public ApplicationConfiguration withPackages(List<String> packages) {
        for (String p : packages) {
            if (isPackage(p)) {
                addPackage(p);
            }
        }
        return this;
    }

    public ApplicationConfiguration withConnectionSource(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
        return this;
    }

    public ApplicationConfiguration withConnection(Connection connection) {
        this.connectionSource = new SingletonConnectionSource(connection);
        return this;
    }

    public ApplicationConfiguration withServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
        return this;
    }

    public ApplicationConfiguration withApplicationInitializerClass(Class<? extends ApplicationInitializer> applicationInitializerClass) {
        this.applicationInitializerClass = applicationInitializerClass;
        return this;
    }

    public ApplicationConfiguration withDevMode(boolean mode) {
        this.devMode = mode;
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
        this.withPackages(packages);
    }

    public ConnectionSource getConnectionSource() {
        return connectionSource;
    }

    public void setConnectionSource(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
    }

    public boolean isDevMode() {
        return devMode;
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

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

    public Class<? extends ApplicationInitializer> getApplicationInitializerClass() {
        return applicationInitializerClass;
    }

    public void setApplicationInitializerClass(Class<? extends ApplicationInitializer> applicationInitializerClass) {
        this.applicationInitializerClass = applicationInitializerClass;
    }

    @SuppressWarnings("rawtypes")
    public Map getProperties() {
        return properties;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setProperties(Map properties) {
        this.properties.clear();
        this.properties.putAll(properties);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public boolean isInitializeClass() {
        return initializeClass;
    }

    public void setInitializeClass(boolean initializeClass) {
        this.initializeClass = initializeClass;
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

    public static final class SingletonConnectionSource implements ConnectionSource {

        private Connection connection;

        public SingletonConnectionSource(Connection connection) {
            this.connection = connection;
        }

        @Override
        public boolean isValid() {
            try {
                return !connection.isClosed();
            } catch (SQLException e) {
                return false;
            }
        }

        @Override
        public Connection getConnection() throws SQLException {
            return connection;
        }

    }

    public static final ApplicationConfiguration unmodifiableApplicationConfig(ApplicationConfiguration cfg) {
        return new UnmodifiableApplicationConfiguration(cfg);
    }

    @SuppressWarnings("rawtypes")
    private static final class UnmodifiableApplicationConfiguration extends ApplicationConfiguration {

        private ApplicationConfiguration cfg;

        public UnmodifiableApplicationConfiguration(ApplicationConfiguration applicationConfiguration) {
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
        public ApplicationConfiguration withConnectionSource(ConnectionSource connectionSource) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ApplicationConfiguration withConnection(Connection connection) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ApplicationConfiguration withServletContext(ServletContext servletContext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ApplicationConfiguration withApplicationInitializerClass(Class<? extends ApplicationInitializer> applicationInitializerClass) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setServletContext(ServletContext servletContext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setPackages(List<String> packages) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setConnectionSource(ConnectionSource connectionSource) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setApplicationInitializerClass(Class<? extends ApplicationInitializer> applicationInitializerClass) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setProperties(Map properties) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ApplicationConfiguration withDevMode(boolean mode) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setDevMode(boolean devMode) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ApplicationConfiguration withInitializeClass(boolean initialize) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setInitializeClass(boolean initializeClass) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isInitializeClass() {
            return cfg.isInitializeClass();
        }

        @Override
        public boolean isDevMode() {
            return cfg.isDevMode();
        }

        @Override
        public ServletContext getServletContext() {
            return cfg.getServletContext();
        }

        @Override
        public List<String> getPackages() {
            return Collections.unmodifiableList(cfg.getPackages());
        }

        @Override
        public ConnectionSource getConnectionSource() {
            return cfg.getConnectionSource();
        }

        @Override
        public Class<? extends ApplicationInitializer> getApplicationInitializerClass() {
            return cfg.getApplicationInitializerClass();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Map getProperties() {
            return Collections.unmodifiableMap(cfg.getProperties());
        }

        @Override
        public Object getProperty(String key) {
            return cfg.getProperty(key);
        }

    }
}

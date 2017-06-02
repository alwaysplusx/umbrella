package com.harmony.umbrella.context;

import static com.harmony.umbrella.context.WebXmlConstant.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.springframework.util.ClassUtils;

import com.harmony.umbrella.context.ApplicationContext.ApplicationContextInitializer;
import com.harmony.umbrella.core.ConnectionSource;
import com.harmony.umbrella.util.ClassFilterFeature;
import com.harmony.umbrella.util.StringUtils;

/**
 * 应用程序配置构建builder, 依赖servletContext来创建应用配置
 * 
 * @author wuxii@foxmail.com
 */
public class ApplicationConfigurationBuilder {

    public static final String APPLICATION_PACKAGE;
    public static final String APPLICATION_DATASOURCE;

    static {
        APPLICATION_PACKAGE = System.getProperty(CONTEXT_PARAM_SCAN_PACKAGES, CONTEXT_PARAM_SCAN_PACKAGES_VALUE);
        APPLICATION_DATASOURCE = System.getProperty(CONTEXT_PARAM_DATASOURCE, CONTEXT_PARAM_DATASOURCE_VALUE);
    }

    protected static final Pattern PACKAGE_PATTERN = Pattern.compile("^[a-zA-Z]+[0-9a-zA-Z]*");

    private ServletContext servletContext;
    private Class<? extends ApplicationContextInitializer> applicationContextInitializerClass;

    private final Set<String> scanPackages = new LinkedHashSet<>();
    private final Map properties = new LinkedHashMap<>();
    private final List<ConnectionSource> connectionSources = new ArrayList<>();
    private final List<Class<? extends Runnable>> shutdownHookClasses = new ArrayList<>();

    public static ApplicationConfigurationBuilder create() {
        return new ApplicationConfigurationBuilder();
    }

    public static ApplicationConfiguration emptyApplicationConfiguration() {
        return EMPTY_APP_CONFIG;
    }

    public static ApplicationConfiguration unmodifiableApplicationConfiguation(ApplicationConfiguration cfg) {
        return new UnmodifiableAppConfig(cfg);
    }

    protected ApplicationConfigurationBuilder() {
    }

    public ApplicationConfigurationBuilder addScanPackage(String pkg) {
        if (!isPackage(pkg)) {
            throw new IllegalArgumentException(pkg + " is not a vaild package name");
        }
        addPackage(pkg);
        return this;
    }

    public ApplicationConfigurationBuilder addDataSource(String jndi) throws NamingException {
        return addDataSource(jndi, null);
    }

    public ApplicationConfigurationBuilder addDataSource(String jndi, Properties contextProperties) throws NamingException {
        DataSource ds = (DataSource) lookup(jndi, contextProperties);
        return addDataSource(ds);
    }

    public ApplicationConfigurationBuilder addDataSource(DataSource dataSource) {
        connectionSources.add(new DataSourceConnectionSource(dataSource));
        return this;
    }

    public ApplicationConfigurationBuilder addProperty(String key, Object value) {
        properties.put(key, value);
        return this;
    }

    public ApplicationConfigurationBuilder addShutdownHook(Class<? extends Runnable> hookClass) {
        if (hookClass == null || !Runnable.class.isAssignableFrom(hookClass) || !ClassFilterFeature.NEWABLE.accept(hookClass)) {
            throw new IllegalArgumentException(hookClass + " not type of " + Runnable.class);
        }
        shutdownHookClasses.add(hookClass);
        return this;
    }

    public ApplicationConfigurationBuilder setApplicationContextInitializer(Class<? extends ApplicationContextInitializer> initializerClass) {
        this.applicationContextInitializerClass = initializerClass;
        return this;
    }

    public ApplicationConfiguration build() {
        AppConfig cfg = new AppConfig();
        cfg.servletContext = this.servletContext;
        cfg.applicationContextInitializerClass = this.applicationContextInitializerClass;
        cfg.scanPackages = Collections.unmodifiableSet(this.scanPackages);
        cfg.properties = Collections.unmodifiableMap(this.properties);
        cfg.connectionSources = Collections.unmodifiableList(this.connectionSources);
        cfg.shutdownHookClasses = Collections.unmodifiableList(this.shutdownHookClasses);
        return cfg;
    }

    public ApplicationConfigurationBuilder apply(ServletContext servletContext) throws ServletException {
        if (servletContext == null) {
            throw new IllegalArgumentException("applied servlet context is null");
        }
        this.servletContext = servletContext;
        ClassLoader loader = ClassUtils.getDefaultClassLoader();
        // application initializer
        String initializerName = getInitParameter(CONTEXT_PARAM_INITIALIZER);
        if (initializerName != null) {
            try {
                setApplicationContextInitializer((Class<? extends ApplicationContextInitializer>) ClassUtils.forName(initializerName, loader));
            } catch (ClassNotFoundException e) {
                throw new ServletException("Can't init class " + initializerName, e);
            } catch (ClassCastException e) {
                throw new ServletException("initializer type mismatch " + initializerName, e);
            }
        }

        // scan-packages
        String[] packages = getInitParameters(CONTEXT_PARAM_SCAN_PACKAGES, APPLICATION_PACKAGE);
        for (String pkg : packages) {
            try {
                addScanPackage(pkg);
            } catch (IllegalArgumentException e) {
                servletContext.log(e.getMessage());
            }
        }

        // connection source
        String[] jndis = getInitParameters(CONTEXT_PARAM_DATASOURCE, APPLICATION_DATASOURCE);
        for (String jndi : jndis) {
            try {
                addDataSource(jndi);
            } catch (NamingException e) {
                servletContext.log("Can not connection to datasource " + jndi);
            }
        }

        // shutdown hooks
        String[] shutdownHookNames = getInitParameters(CONTEXT_PARAM_SHUTDOWN_HOOKS);
        for (String hook : shutdownHookNames) {
            try {
                addShutdownHook((Class<? extends Runnable>) ClassUtils.forName(hook, loader));
            } catch (ClassNotFoundException e) {
                servletContext.log("shutdown hook " + hook + " not found", e);
            } catch (IllegalArgumentException e) {
                servletContext.log(e.getMessage());
            }
        }

        // config properties
        Enumeration<String> initParameterNames = servletContext.getInitParameterNames();
        for (; initParameterNames.hasMoreElements();) {
            String name = initParameterNames.nextElement();
            addProperty(name, servletContext.getInitParameter(name));
        }
        return this;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    private void addPackage(String p) {
        Iterator<String> it = scanPackages.iterator();
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
        scanPackages.add(p);
    }

    private boolean isPackage(String p) {
        if (p == null) {
            return false;
        }
        if (Package.getPackage(p) != null) {
            return true;
        }
        StringTokenizer st = new StringTokenizer(p, ".");
        for (; st.hasMoreTokens();) {
            if (!PACKAGE_PATTERN.matcher(st.nextToken()).matches()) {
                return false;
            }
        }
        return true;
    }

    protected final String getInitParameter(String key) {
        return servletContext.getInitParameter(key);
    }

    private String[] getInitParameters(String key, String... defaultValue) {
        String value = getInitParameter(key);
        return value != null ? StringUtils.tokenizeToStringArray(value, ",") : defaultValue;
    }

    protected final Object lookup(String jndi, Properties properties) throws NamingException {
        return new InitialContext(properties).lookup(jndi);
    }

    static final class DataSourceConnectionSource implements ConnectionSource {

        private DataSource datasource;

        public DataSourceConnectionSource(DataSource datasource) {
            this.datasource = datasource;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return datasource.getConnection();
        }

    }

    private static final AppConfig EMPTY_APP_CONFIG;

    static {
        EMPTY_APP_CONFIG = new AppConfig();
        EMPTY_APP_CONFIG.scanPackages = Collections.emptySet();
        EMPTY_APP_CONFIG.properties = Collections.emptyMap();
        EMPTY_APP_CONFIG.connectionSources = Collections.emptyList();
        EMPTY_APP_CONFIG.shutdownHookClasses = Collections.emptyList();
    }

    private static class UnmodifiableAppConfig implements ApplicationConfiguration {

        private ApplicationConfiguration cfg;

        private UnmodifiableAppConfig(ApplicationConfiguration cfg) {
            this.cfg = cfg;
        }

        @Override
        public Set<String> getScanPackages() {
            return Collections.unmodifiableSet(cfg.getScanPackages());
        }

        @Override
        public Class<? extends ApplicationContextInitializer> getApplicationContextInitializerClass() {
            return cfg.getApplicationContextInitializerClass();
        }

        @Override
        public ServletContext getServletContext() {
            return cfg.getServletContext();
        }

        @Override
        public List<ConnectionSource> getConnectionSources() {
            return Collections.unmodifiableList(cfg.getConnectionSources());
        }

        @Override
        public Object getProperty(String key) {
            return cfg.getProperty(key);
        }

        @Override
        public String getStringProperty(String key) {
            return cfg.getStringProperty(key);
        }

        @Override
        public String getStringProperty(String key, String def) {
            return cfg.getStringProperty(key, def);
        }

        @Override
        public Map getApplicationProperties() {
            return Collections.unmodifiableMap(cfg.getApplicationProperties());
        }

        @Override
        public Runnable[] getShutdownHooks() {
            Runnable[] hooks = cfg.getShutdownHooks();
            Runnable[] result = new Runnable[hooks.length];
            System.arraycopy(hooks, 0, result, 0, hooks.length);
            return result;
        }

    }

    private static class AppConfig implements ApplicationConfiguration {

        private ServletContext servletContext;
        private Class<? extends ApplicationContextInitializer> applicationContextInitializerClass;
        private Set<String> scanPackages = new LinkedHashSet<>();
        private Map properties = new LinkedHashMap<>();
        private List<ConnectionSource> connectionSources = new ArrayList<>();
        private List<Class<? extends Runnable>> shutdownHookClasses = new ArrayList<>();

        @Override
        public Set<String> getScanPackages() {
            return scanPackages;
        }

        @Override
        public Class<? extends ApplicationContextInitializer> getApplicationContextInitializerClass() {
            return applicationContextInitializerClass;
        }

        @Override
        public ServletContext getServletContext() {
            return servletContext;
        }

        @Override
        public List<ConnectionSource> getConnectionSources() {
            return connectionSources;
        }

        @Override
        public Object getProperty(String key) {
            return properties.get(key);
        }

        @Override
        public String getStringProperty(String key) {
            return getStringProperty(key, null);
        }

        @Override
        public String getStringProperty(String key, String def) {
            Object o = properties.get(key);
            return o == null ? def : o.toString();
        }

        @Override
        public Map getApplicationProperties() {
            return properties;
        }

        @Override
        public Runnable[] getShutdownHooks() {
            List<Runnable> hooks = new ArrayList<>();
            try {
                for (Class<? extends Runnable> hookClass : shutdownHookClasses) {
                    hooks.add(hookClass.newInstance());
                }
            } catch (Exception e) {
                // checked
            }
            return hooks.toArray(new Runnable[hooks.size()]);
        }

    }

}

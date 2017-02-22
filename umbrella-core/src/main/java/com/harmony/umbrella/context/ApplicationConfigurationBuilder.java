package com.harmony.umbrella.context;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.naming.InitialContext;
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

    public static final String INIT_PARAM_DATASOURCE = "harmony.datasource";

    public static final String INIT_PARAM_SCAN_PACKAGES = "harmony.scan-packages";

    public static final String INIT_PARAM_INITIALIZER = "harmony.applicationInitializer";

    public static final String INIT_PARAM_SHUTDOWN_HOOKS = "harmony.shutdownHooks";

    static {
        APPLICATION_PACKAGE = System.getProperty(INIT_PARAM_SCAN_PACKAGES, "com.harmony");
        APPLICATION_DATASOURCE = System.getProperty(INIT_PARAM_DATASOURCE, "jdbc/harmony");
    }

    protected static final Pattern PACKAGE_PATTERN = Pattern.compile("^[a-zA-Z]+[0-9a-zA-Z]*");

    protected ServletContext servletContext;
    private Set<String> scanPackages;
    private Map properties;
    private List<ConnectionSource> connectionSources;
    private List<Class<Runnable>> shutdownHookClasses;
    private Class<? extends ApplicationContextInitializer> applicationContextInitializerClass;

    protected ApplicationConfigurationBuilder() {
    }

    ApplicationConfiguration doBuild(ServletContext servletContext) throws ServletException {
        this.servletContext = servletContext;
        return build();
    }

    public ApplicationConfiguration build() throws ServletException {
        this.scanPackages = new LinkedHashSet<>();
        this.properties = new HashMap<>();
        this.connectionSources = new ArrayList<>();
        this.shutdownHookClasses = new ArrayList<>();
        // scan-packages
        String[] pkgs = getInitParameters(INIT_PARAM_SCAN_PACKAGES, APPLICATION_PACKAGE);
        for (String p : pkgs) {
            if (isPackage(p)) {
                addPackage(p);
            }
        }

        // connection source
        String[] jndis = getInitParameters(INIT_PARAM_DATASOURCE, APPLICATION_DATASOURCE);
        for (String jndi : jndis) {
            try {
                DataSource ds = lookup(jndi);
                connectionSources.add(new DataSourceConnectionSource(ds));
            } catch (SQLException e) {
                servletContext.log("Can not connection to datasource " + jndi);
            }
        }

        // shutdown hooks
        String[] shutdownHookNames = getInitParameters(INIT_PARAM_SHUTDOWN_HOOKS);
        for (String hook : shutdownHookNames) {
            try {
                Class hookClass = ClassUtils.forName(hook, ClassUtils.getDefaultClassLoader());
                if (Runnable.class.isAssignableFrom(hookClass) && ClassFilterFeature.NEWABLE.accept(hookClass)) {
                    shutdownHookClasses.add(hookClass);
                } else {
                    servletContext.log(hookClass + " not type of " + Runnable.class);
                }
            } catch (ClassNotFoundException e) {
                servletContext.log("shutdown hook " + hook + " not found", e);
            }
        }

        // application initializer
        String initializerName = getInitParameter(INIT_PARAM_INITIALIZER);
        if (initializerName != null) {
            try {
                this.applicationContextInitializerClass = (Class<? extends ApplicationContextInitializer>) Class.forName(initializerName, true,
                        ClassUtils.getDefaultClassLoader());
            } catch (ClassNotFoundException e) {
                throw new ServletException("Can't init class " + initializerName, e);
            } catch (ClassCastException e) {
                throw new ServletException("initializer type mismatch " + initializerName, e);
            }
        }

        // config properties
        Enumeration<String> initParameterNames = servletContext.getInitParameterNames();
        for (; initParameterNames.hasMoreElements();) {
            String name = initParameterNames.nextElement();
            properties.put(name, servletContext.getInitParameter(name));
        }

        return new ApplicationConfiguration() {

            @Override
            public ServletContext getServletContext() {
                return servletContext;
            }

            @Override
            public Set<String> getScanPackages() {
                return new LinkedHashSet<>(scanPackages);
            }

            @Override
            public Map getApplicationProperties() {
                return Collections.unmodifiableMap(properties);
            }

            @Override
            public Object getProperty(String key) {
                return properties.get(key);
            }

            @Override
            public String getStringProperty(String key) {
                Object v = getProperty(key);
                return v != null ? v.toString() : null;
            }

            @Override
            public List<ConnectionSource> getConnectionSources() {
                return new ArrayList<>(connectionSources);
            }

            @Override
            public Class<? extends ApplicationContextInitializer> getApplicationContextInitializerClass() {
                return applicationContextInitializerClass;
            }

            @Override
            public Runnable[] getShutdownHook() {
                List<Runnable> result = new ArrayList<>(shutdownHookClasses.size());
                for (Class<Runnable> cls : shutdownHookClasses) {
                    try {
                        result.add(cls.newInstance());
                    } catch (Exception e) {
                        throw new IllegalArgumentException("illegal shutdown hook " + cls);
                    }
                }
                return result.toArray(new Runnable[result.size()]);
            }
        };
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

    protected String getInitParameter(String key) {
        return servletContext.getInitParameter(key);
    }

    protected String[] getInitParameters(String key, String... defaultValue) {
        String value = getInitParameter(key);
        return value != null ? StringUtils.tokenizeToStringArray(value, ",") : defaultValue;
    }

    protected DataSource lookup(String jndi) throws SQLException {
        try {
            InitialContext ctx = new InitialContext();
            return (DataSource) ctx.lookup(jndi);
        } catch (Exception e) {
            throw new SQLException("can not connection to datasource " + jndi, e);
        }
    }

    protected static class DataSourceConnectionSource implements ConnectionSource {

        private DataSource datasource;

        public DataSourceConnectionSource(DataSource datasource) {
            this.datasource = datasource;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return datasource.getConnection();
        }

    }
}

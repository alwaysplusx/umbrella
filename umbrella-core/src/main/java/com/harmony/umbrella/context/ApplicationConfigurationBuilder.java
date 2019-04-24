package com.harmony.umbrella.context;

import com.harmony.umbrella.context.metadata.ApplicationMetadata;
import com.harmony.umbrella.core.ConnectionSource;
import com.harmony.umbrella.util.ClassFilterFeature;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

import static com.harmony.umbrella.context.WebXmlConstant.*;

/**
 * 应用程序配置构建builder, 依赖servletContext来创建应用配置
 *
 * @author wuxii@foxmail.com
 */
public class ApplicationConfigurationBuilder {

    public static final String APPLICATION_PACKAGE;
    public static final String APPLICATION_DATASOURCE;

    static {
        APPLICATION_PACKAGE = System.getProperty(APPLICATION_CFG_SCAN_PACKAGES, APPLICATION_CFG_SCAN_PACKAGES_VALUE);
        APPLICATION_DATASOURCE = System.getProperty(APPLICATION_CFG_DATASOURCE, APPLICATION_CFG_DATASOURCE_VALUE);
    }

    protected static final Pattern PACKAGE_PATTERN = Pattern.compile("^[a-zA-Z]+[0-9a-zA-Z]*");

    private String applicationName;
    private final Set<String> scanPackages = new LinkedHashSet<>();
    private final Map<Object, Object> properties = new LinkedHashMap<>();
    private final List<ConnectionSource> connectionSources = new ArrayList<>();
    private final List<Class<? extends Runnable>> shutdownHookClasses = new ArrayList<>();

    public static ApplicationConfigurationBuilder newBuilder() {
        return new ApplicationConfigurationBuilder();
    }

    public static ApplicationConfiguration unmodifiableApplicationConfiguation(ApplicationConfiguration cfg) {
        return new UnmodifiableAppConfig(cfg);
    }

    protected ApplicationConfigurationBuilder() {
    }

    public ApplicationConfigurationBuilder setApplicationName(String appName) {
        this.applicationName = appName;
        return this;
    }

    public ApplicationConfigurationBuilder addScanPackage(String pkg) {
        if (!isPackage(pkg)) {
            throw new IllegalArgumentException(pkg + " is not a vaild package name");
        }
        addPackage(pkg);
        return this;
    }

    public ApplicationConfigurationBuilder addDataSource(DataSource dataSource) {
        connectionSources.add(new DataSourceConnectionSource(dataSource));
        return this;
    }

    public ApplicationConfigurationBuilder putProperties(Map<?, ?> properties) {
        this.properties.putAll(properties);
        return this;
    }

    public ApplicationConfigurationBuilder putProperty(String key, Object value) {
        properties.put(key, value);
        return this;
    }

    public ApplicationConfigurationBuilder addShutdownHook(Class<? extends Runnable> hookClass) {
        if (hookClass == null
                || !Runnable.class.isAssignableFrom(hookClass)
                || !ClassFilterFeature.NEWABLE.accept(hookClass)) {
            throw new IllegalArgumentException(hookClass + " not type of " + Runnable.class);
        }
        shutdownHookClasses.add(hookClass);
        return this;
    }

    public ApplicationConfiguration build() {
        AppConfig cfg = new AppConfig();
        // TODO 生成配置数据
        cfg.applicationName = this.applicationName;
        return cfg;
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
        for (; st.hasMoreTokens(); ) {
            if (!PACKAGE_PATTERN.matcher(st.nextToken()).matches()) {
                return false;
            }
        }
        return true;
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

    private static class AppConfig implements ApplicationConfiguration {

        private String applicationName;
        private ApplicationMetadata metadata;
        private Map<Object, Object> applicationProperties;
        private List<Class<? extends Runnable>> hooks;

        @Override
        public String getApplicationName() {
            return applicationName;
        }

        @Override
        public ApplicationMetadata getApplicationMetadata() {
            return metadata;
        }

        @Override
        public Map<Object, Object> getApplicationProperties() {
            return applicationProperties;
        }

        @Override
        public List<Class<? extends Runnable>> getShutdownHooks() {
            return hooks;
        }

    }

    private static class UnmodifiableAppConfig implements ApplicationConfiguration {

        private ApplicationConfiguration cfg;

        public UnmodifiableAppConfig(ApplicationConfiguration cfg) {
            this.cfg = cfg;
        }

        @Override
        public String getApplicationName() {
            return cfg.getApplicationName();
        }

        @Override
        public ApplicationMetadata getApplicationMetadata() {
            return cfg.getApplicationMetadata();
        }

        @Override
        public Map<Object, Object> getApplicationProperties() {
            return Collections.unmodifiableMap(cfg.getApplicationProperties());
        }

        @Override
        public List<Class<? extends Runnable>> getShutdownHooks() {
            return Collections.unmodifiableList(cfg.getShutdownHooks());
        }
    }

}

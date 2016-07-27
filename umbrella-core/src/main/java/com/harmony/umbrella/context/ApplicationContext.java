package com.harmony.umbrella.context;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.servlet.ServletContext;

import com.harmony.umbrella.asm.ClassReader;
import com.harmony.umbrella.beans.BeanFactory;
import com.harmony.umbrella.beans.BeansException;
import com.harmony.umbrella.beans.SimpleBeanFactory;
import com.harmony.umbrella.context.metadata.ApplicationMetadata;
import com.harmony.umbrella.context.metadata.DatabaseMetadata;
import com.harmony.umbrella.context.metadata.ServerMetadata;
import com.harmony.umbrella.io.Resource;
import com.harmony.umbrella.io.ResourcePatternResolver;
import com.harmony.umbrella.io.support.PathMatchingResourcePatternResolver;
import com.harmony.umbrella.jdbc.ConnectionSource;
import com.harmony.umbrella.jdbc.JndiConnectionSource;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.ClassUtils.ClassFilter;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * 运行的应用的上下文
 * 
 * @author wuxii@foxmail.com
 */
public abstract class ApplicationContext implements BeanFactory {

    protected static final Log LOG = Logs.getLog(ApplicationContext.class);

    protected static final ThreadLocal<CurrentContext> current = new InheritableThreadLocal<CurrentContext>();

    static ServerMetadata serverMetadata = ApplicationMetadata.EMPTY_SERVER_METADATA;

    static DatabaseMetadata databaseMetadata = ApplicationMetadata.EMPTY_DATABASE_METADATA;

    private static ApplicationConfiguration applicationConfiguration;

    /**
     * 只初始化一次
     */
    @SuppressWarnings("rawtypes")
    private static final List<Class> classes = new ArrayList<Class>();

    /**
     * 获取当前应用的应用上下文
     * <p>
     * 加载
     * {@code META-INF/services/com.harmony.umbrella.context.ApplicationContextProvider}
     * 文件中的实际类型来创建
     *
     * @return 应用上下文
     */
    public static final ApplicationContext getApplicationContext() {
        ApplicationContext context = null;
        ServiceLoader<ApplicationContextProvider> providers = ServiceLoader.load(ApplicationContextProvider.class);
        for (ApplicationContextProvider provider : providers) {
            try {
                context = provider.createApplicationContext();
                if (context != null) {
                    LOG.debug("create context [{}] by [{}]", context, provider);
                    break;
                }
            } catch (Exception e) {
                LOG.warn("", e);
            }
        }
        if (context == null) {
            context = SimpleApplicationContext.INSTANCE;
            LOG.debug("no context provider find, use default {}", SimpleApplicationContext.class.getName());
        }
        // 初始化
        context.init();
        return context;
    }

    public static synchronized void initStatic(ApplicationConfiguration applicationConfiguration) {
        if (ApplicationContext.applicationConfiguration == null) {
            LOG.warn("application context already initial");
            return;
        }

        Class<? extends ApplicationInitializer> applicationInitializerClass = applicationConfiguration.getApplicationInitializerClass();
        if (applicationInitializerClass == null) {
            applicationInitializerClass = ApplicationInitializer.class;
        }
        ApplicationInitializer applicationInitializer = ReflectionUtils.instantiateClass(applicationInitializerClass);
        applicationInitializer.init(applicationConfiguration);

        ApplicationContext.applicationConfiguration = applicationConfiguration;
    }

    @SuppressWarnings("rawtypes")
    public static Class[] getApplicationClasses() {
        return classes.toArray(new Class[classes.size()]);
    }

    @SuppressWarnings("rawtypes")
    public static Class[] getApplicationClasses(ClassFilter filter) {
        List<Class> result = new ArrayList<Class>();
        for (Class c : classes) {
            if (filter.accept(c)) {
                result.add(c);
            }
        }
        return result.toArray(new Class[result.size()]);
    }

    /**
     * 设置当前线程的用户环境
     *
     * @param currentCtx
     *            用户环境
     */
    static void setCurrentContext(CurrentContext cc) {
        current.set(cc);
    }

    /**
     * 获取当前线程的用户环境
     *
     * @return 用户环境
     */
    public static CurrentContext getCurrentContext() {
        return current.get();
    }

    public static ServerMetadata getServerMetadata() {
        return serverMetadata;
    }

    public static DatabaseMetadata getDatabaseMetadata() {
        return databaseMetadata;
    }

    public static ApplicationConfiguration getApplicationConfiguration() {
        return new UnmodifiableApplicationConfiguration(applicationConfiguration);
    }

    // application scope

    /**
     * 初始化应用上下文
     */
    public abstract void init();

    /**
     * 销毁应用上下文
     */
    public abstract void destroy();

    /**
     * bean 工厂
     * 
     * @return bean 工厂
     */
    protected abstract BeanFactory getBeanFactory();

    @Override
    public <T> T getBean(Class<T> beanClass) throws BeansException {
        return getBean(beanClass);
    }

    @Override
    public <T> T getBean(Class<T> beanClass, String scope) throws BeansException {
        return getBeanFactory().getBean(beanClass, scope);
    }

    @Override
    public <T> T getBean(String beanName) throws BeansException {
        return getBeanFactory().getBean(beanName);
    }

    @Override
    public <T> T getBean(String beanName, String scope) throws BeansException {
        return getBeanFactory().getBean(beanName, scope);
    }

    /**
     * 将包名转化为需要扫描的路径
     * 
     * @param pkg
     *            包名
     * @return 扫描路径
     */
    private static String toResourcePath(String pkg) {
        return ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + pkg.replace(".", "/") + "/**/*.class";
    }

    private static final class UnmodifiableApplicationConfiguration extends ApplicationConfiguration {

        private ApplicationConfiguration applicationConfiguration;

        public UnmodifiableApplicationConfiguration(ApplicationConfiguration applicationConfiguration) {
            this.applicationConfiguration = applicationConfiguration;
        }

        @Override
        public ApplicationConfiguration withServletContext(ServletContext servletContext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ApplicationConfiguration withPackage(String... pkg) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ApplicationConfiguration withConnectionSource(ConnectionSource... connectionSource) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ApplicationConfiguration withProperty(String key, Object value) {
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
        public void setConnectionSources(List<ConnectionSource> connectionSources) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setProperties(Map<String, Object> properties) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setApplicationInitializerClass(Class<? extends ApplicationInitializer> applicationInitializerClass) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ServletContext getServletContext() {
            return applicationConfiguration.getServletContext();
        }

        @Override
        public List<String> getPackages() {
            return Collections.unmodifiableList(applicationConfiguration.getPackages());
        }

        @Override
        public List<ConnectionSource> getConnectionSources() {
            return applicationConfiguration.getConnectionSources();
        }

        @Override
        public Map<String, Object> getProperties() {
            return Collections.unmodifiableMap(applicationConfiguration.getProperties());
        }

        @Override
        public Class<? extends ApplicationInitializer> getApplicationInitializerClass() {
            return applicationConfiguration.getApplicationInitializerClass();
        }
    }

    public static class ApplicationInitializer {

        private static final Log log = Logs.getLog(ApplicationInitializer.class);

        public static final String INIT_PARAM_DATASOURCE = "datasource";

        public static final String INIT_PARAM_PACKAGES = "packages";

        public ApplicationInitializer() {
        }

        public final void init(ApplicationConfiguration applicationConfiguration) {

            initServer(applicationConfiguration);

            initDatabase(applicationConfiguration);

            initApplicationClasses(applicationConfiguration);

            initCustomer(applicationConfiguration);

        }

        protected void initServer(ApplicationConfiguration applicationConfiguration) {
            ServletContext servletContext = applicationConfiguration.getServletContext();
            if (servletContext == null) {
                log.warn("servlet context not set, server metadata could not be initialized");
                return;
            }
            serverMetadata = ApplicationMetadata.getServerMetadata(servletContext);
        }

        protected void initDatabase(ApplicationConfiguration applicationConfiguration) {
            ConnectionSource connectionSource = null;
            List<ConnectionSource> connectionSources = applicationConfiguration.getConnectionSources();
            if (connectionSources != null && !connectionSources.isEmpty()) {
                connectionSource = connectionSources.get(0);
            }
            if (connectionSource == null) {
                ServletContext servletContext = applicationConfiguration.getServletContext();
                if (servletContext != null) {
                    String datasourceJndi = servletContext.getInitParameter(INIT_PARAM_DATASOURCE);
                    if (StringUtils.isNotBlank(datasourceJndi)) {
                        connectionSource = new JndiConnectionSource(datasourceJndi);
                    }
                }
            }
            if (connectionSource == null) {
                log.warn("connection source not set, database metadata could not be initialized");
                return;
            }
            try {
                databaseMetadata = ApplicationMetadata.getDatabaseMetadata(connectionSource);
            } catch (SQLException e) {
                log.error("initial database metadata failed", e);
            }
        }

        @SuppressWarnings("rawtypes")
        protected void initApplicationClasses(ApplicationConfiguration applicationConfiguration) {
            classes.clear();
            List<String> packages = applicationConfiguration.getPackages();
            if (packages == null || packages.isEmpty()) {
                ServletContext servletContext = applicationConfiguration.getServletContext();
                if (servletContext != null) {
                    String pkgs = servletContext.getInitParameter(INIT_PARAM_PACKAGES);
                    if (StringUtils.isNotBlank(pkgs)) {
                        packages = Arrays.asList(StringUtils.split(pkgs, ",", true));
                    }
                }
            }

            if (packages == null || packages.isEmpty()) {
                log.warn("packages not set, application classes could not be initialized");
                return;
            }

            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            for (String pkg : packages) {
                String resourcePath = toResourcePath(pkg);
                try {
                    Resource[] resources = resourcePatternResolver.getResources(resourcePath);
                    for (Resource resource : resources) {
                        Class<?> clazz = forClass(resource);
                        if (clazz != null && !classes.contains(clazz)) {
                            classes.add(clazz);
                        }
                    }
                } catch (IOException e) {
                    log.error("{} package not found", pkg, e);
                }
            }

            Collections.sort(classes, new Comparator<Class>() {

                @Override
                public int compare(Class o1, Class o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
        }

        protected void initCustomer(ApplicationConfiguration applicationConfiguration) {

        }

    }

    private static Class<?> forClass(Resource resource) {
        InputStream is = null;
        try {
            is = resource.getInputStream();
            return forName(new ClassReader(is).getClassName());
        } catch (IOException e) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    private static Class<?> forName(String className) {
        try {
            return Class.forName(className.replace("/", "."), true, ClassUtils.getDefaultClassLoader());
        } catch (Error e) {
            LOG.warn("{} in classpath jar no fully configured, {}", className, e.toString());
        } catch (Throwable e) {
            LOG.error("{}", className, e);
        }
        return null;
    }

    private static final class SimpleApplicationContext extends ApplicationContext {

        private static final SimpleApplicationContext INSTANCE = new SimpleApplicationContext();

        private BeanFactory beanFactory = new SimpleBeanFactory();

        @Override
        public void init() {
        }

        @Override
        public void destroy() {
        }

        @Override
        public BeanFactory getBeanFactory() {
            return beanFactory;
        }

    }
}

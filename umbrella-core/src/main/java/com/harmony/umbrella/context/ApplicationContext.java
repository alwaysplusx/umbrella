package com.harmony.umbrella.context;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Vector;

import javax.servlet.ServletContext;

import com.harmony.umbrella.asm.ClassReader;
import com.harmony.umbrella.context.metadata.ApplicationMetadata;
import com.harmony.umbrella.context.metadata.DatabaseMetadata;
import com.harmony.umbrella.context.metadata.ServerMetadata;
import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.BeansException;
import com.harmony.umbrella.core.ConnectionSource;
import com.harmony.umbrella.core.SimpleBeanFactory;
import com.harmony.umbrella.io.Resource;
import com.harmony.umbrella.io.support.PathMatchingResourcePatternResolver;
import com.harmony.umbrella.io.support.ResourcePatternResolver;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.ClassUtils.ClassFilter;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * 运行的应用的上下文
 * 
 * @author wuxii@foxmail.com
 */
public abstract class ApplicationContext implements BeanFactory {

    protected static final Log LOG = Logs.getLog(ApplicationContext.class);

    protected static final ThreadLocal<CurrentContext> current = new InheritableThreadLocal<CurrentContext>();

    @SuppressWarnings("rawtypes")
    private static final List<Class> classes = new Vector<Class>();

    private static ServerMetadata serverMetadata = ApplicationMetadata.EMPTY_SERVER_METADATA;

    private static List<DatabaseMetadata> databaseMetadatas = new ArrayList<DatabaseMetadata>();

    private static ApplicationConfiguration applicationConfiguration;

    // FIXME add application destroy hook
    public static synchronized void initStatic(ApplicationConfiguration appConfig) {
        if (applicationConfiguration != null) {
            LOG.warn("application metadata already initial");
            return;
        }
        ApplicationContextInitializer applicationInitializer = null;
        Class<? extends ApplicationContextInitializer> applicationInitializerClass = appConfig.getApplicationContextInitializerClass();

        if (applicationInitializerClass == null) {
            applicationInitializerClass = ApplicationContextInitializer.class;
        }

        applicationInitializer = ReflectionUtils.instantiateClass(applicationInitializerClass);
        // 初始化应用程序
        applicationInitializer.init(appConfig);

        applicationConfiguration = ApplicationConfiguration.unmodifiableApplicationConfig(appConfig);

    }

    public static ApplicationConfiguration getApplicationConfiguration() {
        return applicationConfiguration;
    }

    /**
     * 获取当前应用的应用上下文
     * <p>
     * 加载 {@code META-INF/services/com.huiju.module.context.ContextProvider}
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

    public static DatabaseMetadata[] getDatabaseMetadatas() {
        return databaseMetadatas.toArray(new DatabaseMetadata[0]);
    }

    /**
     * 设置当前线程的用户环境
     *
     * @param cc
     *            用户环境
     */
    static void setCurrentContext(CurrentContext cc) {
        current.set(cc);
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

    // application bean scope

    public ApplicationContext() {
    }

    public abstract BeanFactory getBeanFactory();

    /**
     * 初始化bean工厂
     */
    public abstract void init();

    /**
     * 销毁bean工厂
     */
    public abstract void destroy();

    @Override
    public <T> T getBean(Class<T> beanClass) throws BeansException {
        return getBeanFactory().getBean(beanClass);
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

    private static Class<?> forClass(Resource resource, boolean initialize) {
        InputStream is = null;
        try {
            is = resource.getInputStream();
            return forName(new ClassReader(is).getClassName(), initialize);
        } catch (IOException e) {
            LOG.error(e);
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

    private static Class<?> forName(String className, boolean initialize) {
        try {
            return Class.forName(className.replace("/", "."), initialize, ClassUtils.getDefaultClassLoader());
        } catch (Error e) {
            LOG.warn("{} in classpath jar no fully configured, {}", className, e.toString());
        } catch (Throwable e) {
            LOG.error("{}", className, e);
        }
        return null;
    }

    public static class ApplicationContextInitializer {

        protected static final Log log = Logs.getLog(ApplicationContextInitializer.class);

        protected ApplicationContextInitializer() {
        }

        final void init(ApplicationConfiguration applicationConfiguration) {

            new InternalApplicationInitializer(applicationConfiguration).init();

            initCustomer(applicationConfiguration);
        }

        protected void initCustomer(ApplicationConfiguration applicationConfiguration) {

        }

    }

    private static final class InternalApplicationInitializer {

        private ApplicationConfiguration cfg;

        public InternalApplicationInitializer(ApplicationConfiguration applicationConfiguration) {
            this.cfg = applicationConfiguration;
        }

        public void init() {

            init_server();

            init_database();

            init_application_classes();

        }

        private void init_server() {
            ServletContext servletContext = cfg.getServletContext();
            if (servletContext == null) {
                LOG.warn("servlet context not set, server metadata could not be initialized");
                return;
            }
            serverMetadata = ApplicationMetadata.getServerMetadata(servletContext);
        }

        private void init_database() {
            List<ConnectionSource> css = cfg.getConnectionSources();
            if (css == null || css.isEmpty()) {
                LOG.warn("connection source not set, database metadata could not be initialized");
                return;
            }
            for (ConnectionSource cs : css) {
                try {
                    databaseMetadatas.add(ApplicationMetadata.getDatabaseMetadata(cs));
                } catch (SQLException e) {
                    LOG.error("initial database metadata failed", e);
                }
            }
        }

        @SuppressWarnings("rawtypes")
        private void init_application_classes() {
            List<String> packages = cfg.getPackages();
            if (packages != null && !packages.isEmpty()) {
                classes.clear();
                ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
                for (String pkg : packages) {
                    String resourcePath = toResourcePath(pkg);
                    try {
                        Resource[] resources = resourcePatternResolver.getResources(resourcePath);
                        for (Resource resource : resources) {
                            Class<?> clazz = forClass(resource, cfg.isInitializedClassWhenScan());
                            if (clazz != null && !classes.contains(clazz)) {
                                classes.add(clazz);
                            }
                        }
                    } catch (IOException e) {
                        LOG.error("{} package not found", pkg, e);
                    }
                }

                Collections.sort(classes, new Comparator<Class>() {

                    @Override
                    public int compare(Class o1, Class o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
            }
        }

    }

    private static final class SimpleApplicationContext extends ApplicationContext {

        private static final SimpleApplicationContext INSTANCE = new SimpleApplicationContext();
        private static BeanFactory beanFactory = SimpleBeanFactory.INSTANCE;

        public SimpleApplicationContext() {
        }

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

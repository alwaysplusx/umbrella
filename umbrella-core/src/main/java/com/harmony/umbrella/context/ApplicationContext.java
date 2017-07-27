package com.harmony.umbrella.context;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletContext;

import com.harmony.umbrella.context.metadata.ApplicationMetadata;
import com.harmony.umbrella.context.metadata.DatabaseMetadata;
import com.harmony.umbrella.context.metadata.ServerMetadata;
import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.BeansException;
import com.harmony.umbrella.core.ConnectionSource;
import com.harmony.umbrella.core.SimpleBeanFactory;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.ClassFilter;
import com.harmony.umbrella.util.ClassFilterFeature;
import com.harmony.umbrella.util.Scanner;

/**
 * 运行的应用的上下文
 * 
 * @author wuxii@foxmail.com
 */
public abstract class ApplicationContext implements BeanFactory {

    protected static final Log LOG = Logs.getLog(ApplicationContext.class);

    protected static final ThreadLocal<CurrentContext> current = new InheritableThreadLocal<CurrentContext>();

    private static final List<Class> classes = new Vector<Class>();

    private static ServerMetadata serverMetadata = ApplicationMetadata.EMPTY_SERVER_METADATA;

    private static List<DatabaseMetadata> databaseMetadatas = new ArrayList<DatabaseMetadata>();

    private static ApplicationConfiguration applicationConfiguration;

    private static final int STANDBY = 0;
    private static final int STARTED = 1;
    private static final int SHUTDOWN = -1;

    private static final Object applicationStatusLock = new Object();

    private static int applicationStatus;

    private static boolean scaned;

    public static void start(ApplicationConfiguration appConfig) {
        final ApplicationConfiguration cfg = ApplicationConfigurationBuilder.unmodifiableApplicationConfiguation(appConfig);
        synchronized (applicationStatusLock) {
            if (!isStandBy()) {
                LOG.warn("application already started");
                return;
            }
            ApplicationContextInitializer applicationInitializer = null;
            Class<? extends ApplicationContextInitializer> applicationInitializerClass = cfg.getApplicationContextInitializerClass();
            if (applicationInitializerClass == null) {
                applicationInitializerClass = ApplicationContextInitializer.class;
            }
            try {
                applicationInitializer = applicationInitializerClass.newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("illegal application initializer class " + applicationInitializerClass);
            }
            applicationInitializer.init(cfg);
            applicationConfiguration = cfg;
            applicationStatus = STARTED;
        }
    }

    public static void shutdown() {
        synchronized (applicationStatusLock) {
            List<Runnable> hooks = null;
            try {
                hooks = getShutdownHooks();
            } catch (Exception e) {
                throw new ApplicationContextException("can't instance shutdown hooks", e);
            }
            try {
                for (Runnable runnable : hooks) {
                    runnable.run();
                }
            } catch (Throwable e) {
                if (!applicationConfiguration.getBooleanProperty(WebXmlConstant.APPLICATION_CFG_PROPERTIES_FOCUS_SHUTDOWN)) {
                    throw e;
                }
                LOG.error("shutdown hooks mount fail", e);
            }
            classes.clear();
            databaseMetadatas.clear();
            applicationConfiguration = null;
        }
    }

    /**
     * 获取应用的初始化配置, 必须在启动后才能获取
     * 
     * @return 初始化配置
     */
    public static ApplicationConfiguration getApplicationConfiguration() {
        checkApplicationState();
        return applicationConfiguration;
    }

    /**
     * 判断应用是否已经启动
     * 
     * @return true is started
     */
    public static boolean isStarted() {
        synchronized (applicationStatusLock) {
            return applicationStatus == STARTED;
        }
    }

    public static boolean isStandBy() {
        synchronized (applicationStatusLock) {
            return applicationStatus == STANDBY;
        }
    }

    public static boolean isShutdown() {
        synchronized (applicationStatusLock) {
            return applicationStatus == SHUTDOWN;
        }
    }

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
        checkApplicationState();
        ApplicationContext context = null;
        ServiceLoader<ApplicationContextProvider> providers = ServiceLoader.load(ApplicationContextProvider.class);
        for (ApplicationContextProvider provider : providers) {
            Map applicationProperties = new HashMap<>(applicationConfiguration.getApplicationProperties());
            context = provider.createApplicationContext(applicationProperties);
            if (context != null) {
                LOG.debug("create context [{}] by [{}]", context, provider);
                break;
            }
        }
        if (context == null) {
            context = SimpleApplicationContext.INSTANCE;
            LOG.debug("no context provider find, use default {}", SimpleApplicationContext.class.getName());
        }
        context.init();
        return context;
    }

    /**
     * 获取当前线程的用户环境
     *
     * @return 用户环境
     */
    public static CurrentContext getCurrentContext() {
        checkApplicationState();
        return current.get();
    }

    public static ServerMetadata getServerMetadata() {
        checkApplicationState();
        return serverMetadata;
    }

    public static DatabaseMetadata[] getDatabaseMetadatas() {
        checkApplicationState();
        return databaseMetadatas.toArray(new DatabaseMetadata[0]);
    }

    /**
     * 设置当前线程的用户环境
     *
     * @param cc
     *            用户环境
     */
    static void setCurrentContext(CurrentContext cc) {
        checkApplicationState();
        current.set(cc);
    }

    static int getApplicationClassSize() {
        checkApplicationState();
        return scaned ? classes.size() : -1;
    }

    public static Class[] getApplicationClasses(ClassFilter filter) {
        checkApplicationState();
        if (scaned) {
            return get(filter);
        } else {
            synchronized (classes) {
                return get(filter);
            }
        }
    }

    private static Class[] get(ClassFilter filter) {
        List<Class> result = new ArrayList<Class>();
        for (Class c : classes) {
            if (ClassFilterFeature.safetyAccess(filter, c)) {
                result.add(c);
            }
        }
        return result.toArray(new Class[result.size()]);
    }

    protected static final void checkApplicationState() throws ApplicationContextException {
        if (!isStarted()) {
            throw new ApplicationContextException("application not started!");
        }
        if (isShutdown()) {
            throw new ApplicationContextException("application is shutdown!");
        }
    }

    private static List<Runnable> getShutdownHooks() throws Exception {
        ApplicationContext applicationContext = null;
        boolean autowire = applicationConfiguration.getBooleanProperty(WebXmlConstant.APPLICATION_CFG_PROPERTIES_HOOK_AUTOWIRE);
        if (autowire) {
            applicationContext = ApplicationContext.getApplicationContext();
        }
        List<Runnable> result = new ArrayList<>();
        Class<? extends Runnable>[] shutdownHooks = applicationConfiguration.getShutdownHooks();
        for (Class<? extends Runnable> c : shutdownHooks) {
            Runnable runnable = c.newInstance();
            if (autowire) {
                applicationContext.autowrie(runnable);
            }
            result.add(runnable);
        }
        return result;
    }

    // application bean scope

    protected ApplicationContext() {
    }

    public abstract BeanFactory getBeanFactory();

    public void init() {

    }

    public void destroy() {

    }

    @Override
    public void autowrie(Object bean) throws BeansException {
        getBeanFactory().autowrie(bean);
    }

    @Override
    public <T> T getBean(Class<T> beanClass) throws BeansException {
        return getBeanFactory().getBean(beanClass);
    }

    @Override
    public <T> T getBean(String beanName) throws BeansException {
        return getBeanFactory().getBean(beanName);
    }

    @Override
    public <T> T getBean(String beanName, Class<T> requireType) throws BeansException {
        return getBeanFactory().getBean(beanName, requireType);
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

            init_connection_sources();

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

        private void init_connection_sources() {
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

        private void init_application_classes() {
            boolean async = cfg.getBooleanProperty(WebXmlConstant.APPLICATION_CFG_PROPERTIES_SCAN_ASYNC, true);

            if (async) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        scan();
                    }

                }).start();
            } else {
                scan();
            }

        }

        private void scan() {
            synchronized (classes) {
                classes.clear();
                scaned = false;

                Set<String> packages = cfg.getScanPackages();
                boolean initialize = cfg.getBooleanProperty(WebXmlConstant.APPLICATION_CFG_PROPERTIES_SCAN_INIT);

                Collections.addAll(classes, Scanner.scan(packages, initialize, true));

                scaned = true;
            }
        }
    }

    private static final class SimpleApplicationContext extends ApplicationContext {

        private static final SimpleApplicationContext INSTANCE = new SimpleApplicationContext();

        private static BeanFactory beanFactory = SimpleBeanFactory.INSTANCE;

        public SimpleApplicationContext() {
        }

        @Override
        public BeanFactory getBeanFactory() {
            return beanFactory;
        }

    }
}

package com.harmony.umbrella.context;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.ServletContext;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;

import com.harmony.umbrella.asm.ClassReader;
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
import com.harmony.umbrella.util.IOUtils;

/**
 * 运行的应用的上下文
 * 
 * @author wuxii@foxmail.com
 */
public abstract class ApplicationContext implements BeanFactory {

    protected static final Log LOG = Logs.getLog(ApplicationContext.class);

    protected static final ThreadLocal<CurrentContext> current = new InheritableThreadLocal<CurrentContext>();

    private static final List<ClassResource> classResources = new CopyOnWriteArrayList<>();

    private static final List<DatabaseMetadata> databaseMetadatas = new CopyOnWriteArrayList<DatabaseMetadata>();

    private static ServerMetadata serverMetadata = ApplicationMetadata.EMPTY_SERVER_METADATA;

    private static ApplicationConfiguration applicationConfiguration;

    protected static final int STANDBY = 0;
    protected static final int STARTED = 1;
    protected static final int SHUTDOWN = -1;

    protected static final Object applicationStatusLock = new Object();

    private static int applicationStatus;

    public static void start(ApplicationConfiguration appConfig) throws ApplicationContextException {
        final ApplicationConfiguration cfg = ApplicationConfigurationBuilder.unmodifiableApplicationConfiguation(appConfig);
        synchronized (applicationStatusLock) {
            if (!isStandBy()) {
                // application not standby
                throw new ApplicationContextException("unable start application, status not available " + applicationStatus);
            }
            ApplicationContextInitializer applicationInitializer = null;
            Class<? extends ApplicationContextInitializer> applicationInitializerClass = cfg.getApplicationContextInitializerClass();
            if (applicationInitializerClass == null) {
                applicationInitializerClass = ApplicationContextInitializer.class;
            }
            try {
                applicationInitializer = applicationInitializerClass.newInstance();
            } catch (Exception e) {
                throw new ApplicationContextException("illegal application initializer class " + applicationInitializerClass);
            }
            applicationInitializer.init(cfg);
            applicationConfiguration = cfg;
            applicationStatus = STARTED;
        }
    }

    public static void shutdown() {
        synchronized (applicationStatusLock) {
            if (!isStarted()) {
                throw new ApplicationContextException("unable stop application, status not available " + applicationStatus);
            }
            ApplicationContext applicationContext = null;
            boolean autowire = applicationConfiguration.getBooleanProperty(WebXmlConstant.APPLICATION_CFG_PROPERTIES_HOOK_AUTOWIRE);
            boolean focus = applicationConfiguration.getBooleanProperty(WebXmlConstant.APPLICATION_CFG_PROPERTIES_FOCUS_SHUTDOWN);
            Class<? extends Runnable>[] hooks = applicationConfiguration.getShutdownHooks();
            if (hooks != null && hooks.length > 0) {
                for (Class<? extends Runnable> hook : hooks) {
                    try {
                        Runnable runner = hook.newInstance();
                        if (applicationContext == null) {
                            applicationContext = ApplicationContext.getApplicationContext();
                        }
                        if (autowire) {
                            applicationContext.autowrie(runner);
                        }
                        runner.run();
                    } catch (Exception e) {
                        if (!focus) {
                            throw new ApplicationContextException("application shutdown failed", e);
                        }
                        LOG.error("can't run shutdown hook " + hook, e);
                    }
                }
            }
            databaseMetadatas.clear();
            classResources.clear();
            applicationConfiguration = null;
        }
    }

    /**
     * 获取应用的初始化配置, 必须在启动后才能获取
     * 
     * @return 初始化配置
     */
    public static ApplicationConfiguration getApplicationConfiguration() throws ApplicationContextException {
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
    public static final ApplicationContext getApplicationContext() throws ApplicationContextException {
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
        return current.get();
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

    public static ServerMetadata getServerMetadata() throws ApplicationContextException {
        checkApplicationState();
        return serverMetadata;
    }

    public static DatabaseMetadata[] getDatabaseMetadatas() throws ApplicationContextException {
        checkApplicationState();
        return databaseMetadatas.toArray(new DatabaseMetadata[databaseMetadatas.size()]);
    }

    public static Class[] getApplicationClasses() throws ApplicationContextException {
        return filterApplicationClasses(null);
    }

    public static Class[] getApplicationClasses(ClassFilter filter) throws ApplicationContextException {
        if (filter == null) {
            throw new IllegalArgumentException("class filter not allow null");
        }
        return filterApplicationClasses(filter);
    }

    private static Class[] filterApplicationClasses(ClassFilter filter) {
        Set<Class> result = new HashSet<>();
        ClassResource[] resources = getApplicationClassResources();
        for (ClassResource classResource : resources) {
            Class<?> clazz = classResource.forClass();
            if (clazz != null && (filter == null || filter.accept(clazz))) {
                result.add(clazz);
            }
        }
        return result.toArray(new Class[result.size()]);
    }

    public static ClassResource[] getApplicationClassResources() throws ApplicationContextException {
        checkApplicationState();
        return classResources.toArray(new ClassResource[classResources.size()]);
    }

    static int getApplicationClassResourceSize() throws ApplicationContextException {
        checkApplicationState();
        return classResources.size();
    }

    /**
     * 检查当前application是否是启动状态
     * 
     * @throws ApplicationContextException
     */
    protected static final void checkApplicationState() throws ApplicationContextException {
        if (!isStarted()) {
            throw new ApplicationContextException("application not started! " + applicationStatus);
        }
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

        InternalApplicationInitializer(ApplicationConfiguration applicationConfiguration) {
            this.cfg = applicationConfiguration;
        }

        void init() {

            init_server();

            init_connection_sources();

            init_application_class_resources();

        }

        private void init_server() {
            ServletContext servletContext = cfg.getServletContext();
            if (servletContext == null) {
                LOG.warn("servlet context not set, server metadata could not be initialized");
                return;
            }
            ApplicationContext.serverMetadata = ApplicationMetadata.getServerMetadata(servletContext);
        }

        private void init_connection_sources() {
            List<ConnectionSource> css = cfg.getConnectionSources();
            if (css == null || css.isEmpty()) {
                LOG.warn("connection source not set, database metadata could not be initialized");
                return;
            }
            for (ConnectionSource cs : css) {
                try {
                    ApplicationContext.databaseMetadatas.add(ApplicationMetadata.getDatabaseMetadata(cs));
                } catch (SQLException e) {
                    LOG.error("initial database metadata failed", e);
                }
            }
        }

        private void init_application_class_resources() {
            ApplicationContext.classResources.clear();

            Set<String> packages = cfg.getScanPackages();
            Set<ClassResource> result = new HashSet<>();
            ClassResourceScanner scanner = new ClassResourceScanner();
            for (String pkg : packages) {
                String path = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + pkg.replace(".", "/") + "/**/*.class";
                try {
                    result.addAll(scanner.scan(path));
                } catch (IOException e) {
                    LOG.warn("can't scan package {}, will skip this package");
                    if (LOG.isDebugEnabled()) {
                        LOG.warn(e);
                    }
                }
            }

            ApplicationContext.classResources.addAll(result);
            Collections.sort(ApplicationContext.classResources, new Comparator<ClassResource>() {

                @Override
                public int compare(ClassResource o1, ClassResource o2) {
                    return o1.className.compareTo(o2.className);
                }
            });

        }

    }

    public static class ClassResourceScanner {

        private ResourcePatternResolver loader;

        public ClassResourceScanner() {
            this(new PathMatchingResourcePatternResolver());
        }

        public ClassResourceScanner(ResourcePatternResolver loader) {
            this.loader = loader;
        }

        public List<ClassResource> scan(String path) throws IOException {
            List<ClassResource> result = new ArrayList<>();
            Resource[] resources = loader.getResources(path);
            for (Resource resource : resources) {
                ClassResource classResource = readAsClassResource(resource);
                if (classResource != null) {
                    result.add(classResource);
                }
            }
            return result;
        }

        protected ClassResource readAsClassResource(Resource resource) {
            final Set<String> interfaceNames;
            final String superClassName;
            final String className;

            InputStream is = null;

            try {
                is = resource.getInputStream();
                byte[] buf = IOUtils.toByteArray(is);
                ClassReader reader = new ClassReader(buf);

                className = formatClassName(reader.getClassName());
                superClassName = formatClassName(reader.getSuperName());
                interfaceNames = new HashSet<>();
                for (String interfaceName : reader.getInterfaces()) {
                    interfaceNames.add(formatClassName(interfaceName));
                }
                return new ClassResource(className, superClassName, interfaceNames, resource, loader.getClassLoader());
            } catch (Exception e) {
                return null;
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
            }
        }

        private String formatClassName(String name) {
            return name.replace("/", ".");
        }

    }

    public static final class ClassResource {

        private final ClassLoader classLoader;
        private final Resource resource;
        private final String className;
        private final String superClassName;
        private final Set<String> interfaceNames;

        private ClassResource(String className, String superClassName, Set<String> interfaceNames, Resource resource, ClassLoader classLoader) {
            this.classLoader = classLoader;
            this.resource = resource;
            this.className = className;
            this.superClassName = superClassName;
            this.interfaceNames = interfaceNames;
        }

        public Resource getResource() {
            return resource;
        }

        public String getClassName() {
            return className;
        }

        public String getSuperClassName() {
            return superClassName;
        }

        public Set<String> getInterfaceNames() {
            return Collections.unmodifiableSet(interfaceNames);
        }

        public Class<?> forClass() {
            return forClass(classLoader);
        }

        public Class<?> forClass(ClassLoader loader) {
            try {
                return ClassUtils.forName(className, loader);
            } catch (Throwable e) {
            }
            return null;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((className == null) ? 0 : className.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ClassResource other = (ClassResource) obj;
            if (className == null) {
                if (other.className != null)
                    return false;
            } else if (!className.equals(other.className))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return className;
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

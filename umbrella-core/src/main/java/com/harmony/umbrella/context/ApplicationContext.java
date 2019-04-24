package com.harmony.umbrella.context;

import com.harmony.umbrella.context.metadata.*;
import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.PropertyManager;
import com.harmony.umbrella.core.SimpleBeanFactory;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import org.springframework.beans.BeansException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 运行的应用的上下文
 *
 * @author wuxii@foxmail.com
 */
public abstract class ApplicationContext implements BeanFactory {

    protected static final Log LOG = Logs.getLog(ApplicationContext.class);

    protected static final ThreadLocal<CurrentContext> current = new InheritableThreadLocal<>();

    protected static ApplicationConfiguration applicationConfiguration;

    // 应用上下文的生命周期状态 standby -(starting)-> started -(stopping)-> stopped

    protected static final int STANDBY = 0;
    protected static final int STARTING = 1;
    protected static final int STARTED = 2;
    protected static final int STOPPING = 3;
    protected static final int STOPPED = 4;

    private static int applicationStatus;

    public static synchronized void start(ApplicationConfiguration appConfig) throws ApplicationContextException {
        final ApplicationConfiguration cfg = ApplicationConfigurationBuilder.unmodifiableApplicationConfiguation(appConfig);
        if (applicationStatus != STANDBY && applicationStatus != STOPPED) {
            throw new ApplicationContextException("start application failed, status not available " + applicationStatus);
        }
        try {
            applicationStatus = STARTING;
            PropertyManager pm = cfg.getPropertyManager();
            if (pm.getBoolean(WebXmlConstant.APPLICATION_CFG_PROPERTIES_SHOW_INFO, false)
                    || Logs.getLog("com.harmony.umbrella.context").isDebugEnabled()) {
                printInfo(System.out);
            }
            applicationStatus = STARTED;
        } catch (Throwable e) {
            applicationStatus = STANDBY;
            throw e;
        }
    }

    /**
     * 关闭应用上下文, 关闭方法调用后应用上下文状态总是成功更改的(就算是hooks有异常抛出状态依然更改为关闭).
     */
    public static void stop() {
        stop(applicationConfiguration.getPropertyManager().getBoolean(WebXmlConstant.APPLICATION_CFG_PROPERTIES_FOCUS_SHUTDOWN, true));
    }

    /**
     * 关闭应用上下文, 关闭方法调用后应用上下文状态总是成功更改的(就算是hooks有异常抛出状态依然更改为关闭).
     */
    public static synchronized void stop(boolean focus) {
        if (applicationStatus != STARTED) {
            throw new ApplicationContextException("stop application failed, status not available " + applicationStatus);
        }
        try {
            // prepare property and get application context first
            List<Class<? extends Runnable>> hooks = applicationConfiguration.getShutdownHooks();
            // change status
            applicationStatus = STOPPING;
            // run hooks
            if (hooks != null && !hooks.isEmpty()) {
                boolean autowire = applicationConfiguration
                        .getPropertyManager()
                        .getBoolean(WebXmlConstant.APPLICATION_CFG_PROPERTIES_HOOK_AUTOWIRE, false);
                ApplicationContext applicationContext = null;
                if (autowire) {
                    applicationContext = ApplicationContext.getApplicationContext();
                }
                for (Class<? extends Runnable> hook : hooks) {
                    try {
                        Runnable runner = hook.newInstance();
                        if (autowire) {
                            applicationContext.autowire(runner);
                        }
                        runner.run();
                    } catch (Throwable e) {
                        if (!focus) {
                            throw new ApplicationContextException("application shutdown failed", e);
                        }
                        LOG.error("can't run shutdown hook {}", hook, e);
                    }
                }
            }
        } finally {
            applicationConfiguration = null;
            applicationStatus = STOPPED;
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
    public static synchronized boolean isStarted() {
        return applicationStatus == STARTED;

    }

    /**
     * 判断应用是否未启动
     *
     * @return true is stopped
     */
    public static synchronized boolean isStopped() {
        return applicationStatus == STOPPED;
    }

    /**
     * 应用上下文正在启动判断, 不会对锁进行竞争只读取当前应用的状态
     *
     * @return true is starting, false is not
     */
    public static boolean isStarting() {
        return applicationStatus == STARTING;
    }

    /**
     * 应用上下文正在停止判断, 不会对锁进行竞争只读取当前应用的状态
     *
     * @return true is stopping, false is not
     */
    public static boolean isStopping() {
        return applicationStatus == STOPPING;
    }

    /**
     * 获取当前应用的应用上下文
     * <p>
     * 加载 {@code META-INF/services/com.harmony.umbrella.context.ApplicationContextProvider} 文件中的实际类型来创建
     *
     * @return 应用上下文
     */
    public static ApplicationContext getApplicationContext() throws ApplicationContextException {
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
     * @param cc 用户环境
     */
    static void setCurrentContext(CurrentContext cc) {
        current.set(cc);
    }

    /**
     * 检查当前application是否是启动状态
     *
     * @throws ApplicationContextException
     */
    protected static void checkApplicationState() throws ApplicationContextException {
        if (!isStarted()) {
            throw new ApplicationContextException("application not started! " + applicationStatus);
        }
    }

    protected static void printInfo(OutputStream o) {
        StringBuilder out = new StringBuilder();
        ApplicationConfiguration cfg = applicationConfiguration;
        ApplicationMetadata metadata = cfg.getApplicationMetadata();
        ServerMetadata sm = metadata.getServerMetadata();
        JavaMetadata jm = metadata.getJavaMetadata();
        OperatingSystemMetadata osm = metadata.getOperatingSystemMetadata();
        List<DataSourceMetadata> dms = metadata.getDataSourceMetadata();
        List<ClassResource> classResources = metadata.getClassResources();
        out//
                .append("\n############################################################")//
                .append("\n#                   Application Information                #")//
                .append("\n############################################################")//
                .append("\n#                      cpu : ").append(osm.cpu)//
                .append("\n#                  os name : ").append(osm.osName)//
                .append("\n#                time zone : ").append(osm.timeZone)//
                .append("\n#               os version : ").append(osm.osVersion)//
                .append("\n#                user home : ").append(osm.userHome)//
                .append("\n#            file encoding : ").append(osm.fileEncoding)//
                .append("\n#")//
                .append("\n#                 jvm name : ").append(jm.vmName)//
                .append("\n#               jvm vendor : ").append(jm.vmVendor)//
                .append("\n#              jvm version : ").append(jm.vmVersion)//
                .append("\n#");//
        for (DataSourceMetadata dm : dms) {
            out//
                    .append("\n#                 database : ").append(dm.productName)//
                    .append("\n#             database url : ").append(dm.url)//
                    .append("\n#            database user : ").append(dm.userName)//
                    .append("\n#              driver name : ").append(dm.driverName)//
                    .append("\n#           driver version : ").append(dm.driverVersion)//
                    .append("\n#");//
        }
        out//
                .append("\n#          app server type : ").append(sm.serverName)//
                .append("\n#          app server name : ").append(sm.serverInfo)//
                .append("\n#          servlet version : ").append(sm.servletVersion)//
                .append("\n#")//
                .append("\n#                spec name : ").append(jm.specificationName)//
                .append("\n#             spec version : ").append(jm.specificationVersion)//
                .append("\n#                java home : ").append(jm.javaHome)//
                .append("\n#              java vendor : ").append(jm.javaVendor)//
                .append("\n#             java version : ").append(jm.javaVersion)//
                .append("\n#          runtime version : ").append(jm.runtimeVersion)//
                .append("\n#")//
                .append("\n#      class resource size : ").append(classResources.size())//
                .append("\n############################################################")//
                .append("\n\n");
        try {
            OutputStreamWriter writer = new OutputStreamWriter(o);
            writer.write(out.toString());
            writer.flush();
        } catch (IOException e) {
            // ignore
            e.printStackTrace();
        }
    }

    // application bean scope

    protected ApplicationContext() {
    }

    protected abstract BeanFactory getBeanFactory();

    @Override
    public void autowire(Object bean) throws BeansException {
        getBeanFactory().autowire(bean);
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

    private static final class SimpleApplicationContext extends ApplicationContext {

        private static final SimpleApplicationContext INSTANCE = new SimpleApplicationContext();

        private static BeanFactory beanFactory = SimpleBeanFactory.INSTANCE;

        public SimpleApplicationContext() {
        }

        @Override
        protected BeanFactory getBeanFactory() {
            return beanFactory;
        }

    }
}

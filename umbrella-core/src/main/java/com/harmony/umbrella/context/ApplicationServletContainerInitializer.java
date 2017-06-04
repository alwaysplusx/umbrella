package com.harmony.umbrella.context;

import static com.harmony.umbrella.context.WebXmlConstant.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import org.springframework.core.OrderComparator;
import org.springframework.util.ClassUtils;

import com.harmony.umbrella.context.listener.ApplicationDestroyListener;
import com.harmony.umbrella.context.listener.ApplicationEventListener;
import com.harmony.umbrella.context.listener.ApplicationListener;
import com.harmony.umbrella.context.listener.ApplicationStartListener;
import com.harmony.umbrella.context.metadata.ApplicationMetadata;
import com.harmony.umbrella.context.metadata.DatabaseMetadata;
import com.harmony.umbrella.context.metadata.JavaMetadata;
import com.harmony.umbrella.context.metadata.OperatingSystemMetadata;
import com.harmony.umbrella.context.metadata.ServerMetadata;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.ClassFilter;
import com.harmony.umbrella.util.ClassFilterFeature;

/**
 * @author wuxii@foxmail.com
 */
@HandlesTypes({ //
        ApplicationEventListener.class, //
        ApplicationStartListener.class, //
        ApplicationDestroyListener.class, //
        ApplicationListener.class //
})
public class ApplicationServletContainerInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext servletContext) throws ServletException {
        c = new HashSet<>(c == null ? Collections.emptySet() : c);

        final ApplicationConfiguration cfg;
        try {
            cfg = buildApplicationConfiguration(servletContext);
        } catch (Exception e) {
            throw new ServletException("build application configuration failure!", e);
        }

        // init application static first
        ApplicationContext.start(cfg);

        if (cfg.getBooleanProperty(APPLICATION_CFG_PROPERTIES_SHOW_INFO) //
                || Logs.getLog("com.harmony.umbrella.context").isDebugEnabled()) {
            showApplicationInfo();
        }

        // must after application started
        if (cfg.getBooleanProperty(APPLICATION_CFG_PROPERTIES_SCAN_HANDLES_TYPES) || ContextHelper.isWeblogic()) {
            c.addAll(scanApplicationEventListener());
        }

        if (c.isEmpty()) {
            servletContext.log("No application ApplicationInitializer types detected on classpath");
            return;
        }

        ApplicationContext applicationContext = null;
        boolean autowire = cfg.getBooleanProperty(APPLICATION_CFG_PROPERTIES_LISTENER_AUTOWIRE);

        final List<ApplicationEventListener> eventListeners = new ArrayList<ApplicationEventListener>(c.size());

        for (Class<?> cls : c) {
            try {
                ApplicationEventListener listener = (ApplicationEventListener) cls.newInstance();
                if (autowire) {
                    if (applicationContext == null) {
                        applicationContext = ApplicationContext.getApplicationContext();
                    }
                    applicationContext.autowrie(listener);
                }
                eventListeners.add(listener);
            } catch (Throwable e) {
                throw new ServletException("Failed to instantiate ApplicationInitializer class", e);
            }
        }

        OrderComparator.sort(eventListeners);

        servletContext.addListener(new ServletContextListener() {

            @Override
            public void contextInitialized(ServletContextEvent sce) {
                long s = System.currentTimeMillis();
                sce.getServletContext().log("begin start application");
                for (int i = 0, max = eventListeners.size(); i < max; i++) {
                    ApplicationEventListener listener = eventListeners.get(i);
                    if (listener instanceof ApplicationStartListener) {
                        ((ApplicationStartListener) listener).onStartup(cfg);
                    }
                }
                sce.getServletContext().log("application started, use " + (System.currentTimeMillis() - s) + "ms");
            }

            @Override
            public void contextDestroyed(ServletContextEvent sce) {
                long s = System.currentTimeMillis();
                sce.getServletContext().log("begin stop application");
                for (int i = eventListeners.size() - 1; i >= 0; i--) {
                    ApplicationEventListener listener = eventListeners.get(i);
                    if (listener instanceof ApplicationDestroyListener) {
                        ((ApplicationDestroyListener) listener).onDestroy(cfg);
                    }
                }
                ApplicationContext.shutdown();
                sce.getServletContext().log("application stopped, use " + (System.currentTimeMillis() - s) + "ms");
            }

        });

    }

    private ApplicationConfiguration buildApplicationConfiguration(ServletContext servletContext) throws Exception {
        Object cfg = servletContext.getAttribute(CONTEXT_ATTRIBUTE_APP_CONFIG);
        if (cfg != null) {
            return (ApplicationConfiguration) cfg;
        }

        ApplicationConfigurationBuilder builder = null;
        String builderName = servletContext.getInitParameter(APPLICATION_CFG_PROPERTIES_CUSTOM_BUILDER);
        if (builderName != null) {
            Class<?> builderClass = ClassUtils.forName(builderName, ClassUtils.getDefaultClassLoader());
            builder = (ApplicationConfigurationBuilder) builderClass.newInstance();
        } else {
            builder = ApplicationConfigurationBuilder.create();
        }
        return builder.apply(servletContext).build();
    }

    /*
     * under weblogic just load @HandlesTypes in WEB-INF/lib/*.jar, so load @HandlesTypes manually
     */
    protected Set<Class<?>> scanApplicationEventListener() throws ServletException {
        Set<Class<?>> result = new HashSet<>();
        ApplicationContext.getApplicationClasses(new ClassFilter() {
            @Override
            public boolean accept(Class<?> clazz) {
                if (ClassFilterFeature.NEWABLE.accept(clazz)//
                        && !result.contains(clazz)//
                        && ApplicationEventListener.class.isAssignableFrom(clazz))
                    result.add(clazz);
                return false;
            }
        });
        return result;
    }

    protected void showApplicationInfo() {

        StringBuilder out = new StringBuilder();
        ApplicationConfiguration cfg = ApplicationContext.getApplicationConfiguration();
        ServerMetadata serverMetadata = ApplicationContext.getServerMetadata();
        JavaMetadata javaMetadata = ApplicationMetadata.getJavaMetadata();
        OperatingSystemMetadata osMetadata = ApplicationMetadata.getOperatingSystemMetadata();
        DatabaseMetadata[] dms = ApplicationContext.getDatabaseMetadatas();
        out//
                .append("\n############################################################")//
                .append("\n#                   Application Information                #")//
                .append("\n############################################################")//
                .append("\n#                      cpu : ").append(osMetadata.cpu)//
                .append("\n#                  os name : ").append(osMetadata.osName)//
                .append("\n#                time zone : ").append(osMetadata.timeZone)//
                .append("\n#               os version : ").append(osMetadata.osVersion)//
                .append("\n#                user home : ").append(osMetadata.userHome)//
                .append("\n#            file encoding : ").append(osMetadata.fileEncoding)//
                .append("\n#")//
                .append("\n#                 jvm name : ").append(javaMetadata.vmName)//
                .append("\n#               jvm vendor : ").append(javaMetadata.vmVendor)//
                .append("\n#              jvm version : ").append(javaMetadata.vmVersion)//
                .append("\n#");//
        for (DatabaseMetadata dm : dms) {
            out//
                    .append("\n#                 database : ").append(dm.productName)//
                    .append("\n#             database url : ").append(dm.url)//
                    .append("\n#            database user : ").append(dm.userName)//
                    .append("\n#              driver name : ").append(dm.driverName)//
                    .append("\n#           driver version : ").append(dm.driverVersion)//
                    .append("\n#");//
        }
        out//
                .append("\n#          app server type : ").append(serverMetadata.serverName)//
                .append("\n#          app server name : ").append(serverMetadata.serverInfo)//
                .append("\n#          servlet version : ").append(serverMetadata.servletVersion)//
                .append("\n#")//
                .append("\n#                spec name : ").append(javaMetadata.specificationName)//
                .append("\n#             spec version : ").append(javaMetadata.specificationVersion)//
                .append("\n#                java home : ").append(javaMetadata.javaHome)//
                .append("\n#              java vendor : ").append(javaMetadata.javaVendor)//
                .append("\n#             java version : ").append(javaMetadata.javaVersion)//
                .append("\n#          runtime version : ").append(javaMetadata.runtimeVersion)//
                .append("\n#")//
                .append("\n#             app packages : ").append(cfg != null ? cfg.getScanPackages() : null)//
                .append("\n#         app classes size : ").append(ApplicationContext.getApplicationClassSize())//
                .append("\n############################################################")//
                .append("\n\n");
        System.out.println(out.toString());
    }

}

package com.harmony.umbrella.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
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

    private ServletContext servletContext;

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext servletContext) throws ServletException {
        this.servletContext = servletContext;
        c = new HashSet<>(c == null ? Collections.emptySet() : c);

        try {
            final ApplicationConfiguration cfg = buildApplicationConfiguration();

            // init application static first
            ApplicationContext.start(cfg);

            if (Boolean.valueOf(getInitParameter("show-info")) //
                    || Logs.getLog().isDebugEnabled()//
                    || Boolean.valueOf(cfg.getStringProperty("application.show-info"))) {
                showApplicationInfo();
            }
            // must after application static init
            c.addAll(scanApplicationEventListener());

            if (c.isEmpty()) {
                servletContext.log("No application ApplicationInitializer types detected on classpath");
                return;
            }

            final List<ApplicationEventListener> eventListeners = new ArrayList<ApplicationEventListener>(c.size());
            for (Class<?> cls : c) {
                try {
                    eventListeners.add((ApplicationEventListener) cls.newInstance());
                } catch (Throwable e) {
                    throw new ServletException("Failed to instantiate ApplicationInitializer class", e);
                }
            }

            OrderComparator.sort(eventListeners);

            servletContext.addListener(new ServletContextListener() {

                @Override
                public void contextInitialized(ServletContextEvent sce) {
                    for (int i = 0, max = eventListeners.size(); i < max; i++) {
                        ApplicationEventListener listener = eventListeners.get(i);
                        if (listener instanceof ApplicationStartListener) {
                            ((ApplicationStartListener) listener).onStartup(cfg);
                        }
                    }
                }

                @Override
                public void contextDestroyed(ServletContextEvent sce) {
                    for (int i = eventListeners.size() - 1; i >= 0; i--) {
                        ApplicationEventListener listener = eventListeners.get(i);
                        if (listener instanceof ApplicationDestroyListener) {
                            ((ApplicationDestroyListener) listener).onDestroy(cfg);
                        }
                    }
                    ApplicationContext.shutdown();
                }

            });

        } catch (ClassNotFoundException e) {
            throw new ServletException("can't create application configuration builder", e);
        } catch (ClassCastException e) {
            throw new ServletException("application configuration builder type mismatch", e);
        }
    }

    protected Set<Class<?>> scanApplicationEventListener() throws ServletException {
        // under weblogic just load @HandlesTypes in WEB-INF/lib/*.jar, so load @HandlesTypes manually
        Set<Class<?>> result = new HashSet<>();
        if (Boolean.valueOf(getInitParameter("scan-handles-types")) || ContextHelper.isWeblogic()) {
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
        }
        return result;
    }

    private String getInitParameter(String name) {
        return servletContext.getInitParameter(name);
    }

    private ApplicationConfiguration buildApplicationConfiguration() throws ClassNotFoundException, ClassCastException, ServletException {
        ApplicationConfiguration appCfg = null;
        String builderName = getInitParameter("applictionConfigurationBuilder");
        if (builderName != null) {
            ApplicationConfigurationBuilder builder;
            try {
                builder = (ApplicationConfigurationBuilder) ClassUtils.forName(builderName, ClassUtils.getDefaultClassLoader()).newInstance();
                appCfg = builder.doBuild(servletContext);
            } catch (Exception e) {
                throw new IllegalArgumentException("illegal application cofngiuration builder " + builderName);
            }
        }
        if (appCfg == null) {
            ServiceLoader<ApplicationConfigurationBuilder> providers = ServiceLoader.load(ApplicationConfigurationBuilder.class);
            for (ApplicationConfigurationBuilder b : providers) {
                appCfg = b.doBuild(servletContext);
                if (appCfg != null) {
                    servletContext.log("build application configuration with " + b);
                    break;
                }
            }
        }
        return appCfg == null ? new ApplicationConfigurationBuilder().doBuild(servletContext) : appCfg;
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

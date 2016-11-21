package com.harmony.umbrella.web;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import com.harmony.umbrella.context.ApplicationConfiguration;
import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.ApplicationInitializer;
import com.harmony.umbrella.context.ContextHelper;
import com.harmony.umbrella.context.metadata.ApplicationMetadata;
import com.harmony.umbrella.context.metadata.DatabaseMetadata;
import com.harmony.umbrella.context.metadata.JavaMetadata;
import com.harmony.umbrella.context.metadata.OperatingSystemMetadata;
import com.harmony.umbrella.context.metadata.ServerMetadata;
import com.harmony.umbrella.core.annotation.Order;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
@HandlesTypes(ApplicationInitializer.class)
public class ApplicationServletContainerInitializer implements ServletContainerInitializer {

    private static final String INIT_PARAM_APPLICATION_CONFIGURATION_BUILDER = "applicationConfigurationBuilder";

    private static final List<Class> HANDLES_TYPES;

    static {
        Class[] handlesTypes = ApplicationServletContainerInitializer.class.getAnnotation(HandlesTypes.class).value();
        HANDLES_TYPES = new ArrayList<>();
        for (Class c : handlesTypes) {
            if (!HANDLES_TYPES.contains(c)) {
                HANDLES_TYPES.add(c);
            }
        }
    }

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext servletContext) throws ServletException {
        String builderClassName = servletContext.getInitParameter(INIT_PARAM_APPLICATION_CONFIGURATION_BUILDER);
        if (StringUtils.isBlank(builderClassName)) {
            builderClassName = ApplicationConfigurationBuilder.class.getName();
        }
        ApplicationConfigurationBuilder builder;
        try {
            builder = (ApplicationConfigurationBuilder) ReflectionUtils.instantiateClass(builderClassName);
        } catch (ClassNotFoundException e) {
            throw new ServletException("can't create application configuration builder", e);
        }

        final ApplicationConfiguration cfg = builder.build(servletContext);
        // init application static metadata information
        ApplicationContext.initStatic(cfg);

        if (cfg.isDevMode()) {
            showApplicationInfo();
        }

        final ApplicationConfiguration unmodifiableApplicationConfig = ApplicationConfiguration.unmodifiableApplicationConfig(cfg);

        if (c == null) {
            c = new HashSet<>();
        }
        c.addAll(findMoreHandlesTypes());
        List<ApplicationInitializer> initializes = new ArrayList<ApplicationInitializer>();
        if (!c.isEmpty()) {
            for (Class<?> cls : c) {
                if (!cls.isInterface() //
                        && !Modifier.isAbstract(cls.getModifiers()) //
                        && ApplicationInitializer.class.isAssignableFrom(cls)) {
                    try {
                        initializes.add((ApplicationInitializer) cls.newInstance());
                    } catch (Throwable e) {
                        throw new ServletException("Failed to instantiate ApplicationInitializer class", e);
                    }
                }
            }
        }

        if (initializes.isEmpty()) {
            servletContext.log("No application ApplicationInitializer types detected on classpath");
            return;
        }

        Collections.sort(initializes, new Comparator<ApplicationInitializer>() {

            @Override
            public int compare(ApplicationInitializer o1, ApplicationInitializer o2) {
                Order ann1 = o1.getClass().getAnnotation(Order.class);
                Order ann2 = o2.getClass().getAnnotation(Order.class);
                return (ann1 == null || ann2 == null) ? 0 : ann1.value() > ann2.value() ? 1 : ann1.value() == ann2.value() ? 0 : -1;
            }

        });

        for (ApplicationInitializer initializer : initializes) {
            initializer.onStartup(unmodifiableApplicationConfig);
        }
    }

    protected Set<Class<?>> findMoreHandlesTypes() throws ServletException {
        Set<Class<?>> result = new HashSet<>();
        // under weblogic just load @HandlesTypes in WEB-INF/lib/*.jar, so load @HandlesTypes manually
        if (ContextHelper.isWeblogic()) {
            Class[] classes = ApplicationContext.getApplicationClasses();
            for (Class clazz : classes) {
                for (Class ht : HANDLES_TYPES) {
                    if (ht.isAssignableFrom(clazz)) {
                        result.add(ht);
                    }
                }
            }
        }
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
                .append("\n################################################################")//
                .append("\n#                     Application Information                  #")//
                .append("\n################################################################")//
                .append("\n#                   cpu : ").append(osMetadata.cpu)//
                .append("\n#               os name : ").append(osMetadata.osName)//
                .append("\n#             time zone : ").append(osMetadata.timeZone)//
                .append("\n#            os version : ").append(osMetadata.osVersion)//
                .append("\n#             user home : ").append(osMetadata.userHome)//
                .append("\n#         file encoding : ").append(osMetadata.fileEncoding)//
                .append("\n#")//
                .append("\n#              jvm name : ").append(javaMetadata.vmName)//
                .append("\n#            jvm vendor : ").append(javaMetadata.vmVendor)//
                .append("\n#           jvm version : ").append(javaMetadata.vmVersion)//
                .append("\n#");//
        for (DatabaseMetadata dm : dms) {
            out//
                    .append("\n#              database : ").append(dm.productName)//
                    .append("\n#          database url : ").append(dm.url)//
                    .append("\n#         database user : ").append(dm.userName)//
                    .append("\n#           driver name : ").append(dm.driverName)//
                    .append("\n#        driver version : ").append(dm.driverVersion)//
                    .append("\n#");//
        }
        out//
                .append("\n#            app server : ").append(serverMetadata.serverName)//
                .append("\n#       servlet version : ").append(serverMetadata.servletVersion)//
                .append("\n#")//
                .append("\n#             spec name : ").append(javaMetadata.specificationName)//
                .append("\n#          spec version : ").append(javaMetadata.specificationVersion)//
                .append("\n#             java home : ").append(javaMetadata.javaHome)//
                .append("\n#           java vendor : ").append(javaMetadata.javaVendor)//
                .append("\n#          java version : ").append(javaMetadata.javaVersion)//
                .append("\n#       runtime version : ").append(javaMetadata.runtimeVersion)//
                .append("\n#")//
                .append("\n#          app packages : ").append(cfg != null ? cfg.getPackages() : null)//
                .append("\n################################################################")//
                .append("\n\n");
        System.out.println(out.toString());
    }

}

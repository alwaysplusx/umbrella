package com.harmony.umbrella.web;

import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.naming.InitialContext;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import javax.sql.DataSource;

import com.harmony.umbrella.context.ApplicationConfiguration;
import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.WebApplicationInitializer;
import com.harmony.umbrella.context.metadata.ApplicationMetadata;
import com.harmony.umbrella.context.metadata.DatabaseMetadata;
import com.harmony.umbrella.context.metadata.DatabaseMetadata.ConnectionSource;
import com.harmony.umbrella.context.metadata.JavaMetadata;
import com.harmony.umbrella.context.metadata.OperatingSystemMetadata;
import com.harmony.umbrella.context.metadata.ServerMetadata;
import com.harmony.umbrella.core.annotation.Order;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
@HandlesTypes(WebApplicationInitializer.class)
public class ApplicationServletContainerInitializer implements ServletContainerInitializer {

    public static final String INIT_PARAM_DATASOURCE = "datasource";

    public static final String INIT_PARAM_PACKAGES = "packages";

    public static final String INIT_PARAM_DEVMODE = "devMode";

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext servletContext) throws ServletException {
        new WebApplicationContextInitializer(servletContext).startup();
        ApplicationConfiguration applicationCfg = ApplicationContext.getApplicationConfiguration();

        List<WebApplicationInitializer> initializers = new ArrayList<WebApplicationInitializer>();
        if (c != null && c.size() > 0) {
            for (Class<?> cls : c) {
                if (!cls.isInterface() //
                        && !Modifier.isAbstract(cls.getModifiers()) //
                        && WebApplicationInitializer.class.isAssignableFrom(cls)) {
                    try {
                        initializers.add((WebApplicationInitializer) cls.newInstance());
                    } catch (Throwable e) {
                        throw new ServletException("Failed to instantiate WebApplicationInitializer class", e);
                    }
                }
            }
        }

        if (initializers.isEmpty()) {
            servletContext.log("No application WebApplicationInitializer types detected on classpath");
            return;
        }

        Collections.sort(initializers, new Comparator<WebApplicationInitializer>() {

            @Override
            public int compare(WebApplicationInitializer o1, WebApplicationInitializer o2) {
                Order ann1 = o1.getClass().getAnnotation(Order.class);
                Order ann2 = o2.getClass().getAnnotation(Order.class);
                return (ann1 == null || ann2 == null) ? 0 : ann1.value() > ann2.value() ? 1 : ann1.value() == ann2.value() ? 0 : -1;
            }

        });

        for (WebApplicationInitializer initializer : initializers) {
            initializer.onStartup(servletContext, applicationCfg);
        }
    }

    static class WebApplicationContextInitializer {

        private ServletContext servletContext;

        public WebApplicationContextInitializer(ServletContext servletContext) {
            this.servletContext = servletContext;
        }

        public void startup() throws ServletException {
            ApplicationConfiguration appConfig = new ApplicationConfiguration();
            appConfig.withServletContext(servletContext);

            // database connection source
            String jndiName = getInitParam(INIT_PARAM_DATASOURCE, servletContext);
            if (StringUtils.isNotBlank(jndiName)) {
                JndiConnectionSource connectionSource = new JndiConnectionSource(jndiName);
                if (connectionSource.isValid()) {
                    appConfig.withConnectionSource(connectionSource);
                } else {
                    servletContext.log(jndiName + " connection is not avlid");
                }
            } else {
                servletContext.log("unspecified database connection source");
            }

            // application packages
            String[] packages = getInitParams(INIT_PARAM_PACKAGES, ",", servletContext);
            if (packages != null && packages.length > 0) {
                appConfig.withPackages(packages);
            } else {
                servletContext.log("unspecified application package(s)");
            }

            String devMode = getInitParam(INIT_PARAM_DEVMODE, servletContext);
            appConfig.withDevMode(Boolean.valueOf(devMode));

            // init application static metadata information
            ApplicationContext.initStatic(appConfig);

            if (appConfig.isDevMode()) {
                showApplicationInfo();
            }
        }

        private String getInitParam(String key, ServletContext servletContext) {
            return servletContext != null ? servletContext.getInitParameter(key) : null;
        }

        private String[] getInitParams(String key, String delimiter, ServletContext servletContext) {
            String value = getInitParam(key, servletContext);
            return value != null ? StringUtils.tokenizeToStringArray(value, delimiter) : null;
        }

        protected void showApplicationInfo() {

            StringBuilder out = new StringBuilder();

            ApplicationConfiguration cfg = ApplicationContext.getApplicationConfiguration();
            DatabaseMetadata databaseMetadata = ApplicationContext.getDatabaseMetadata();
            ServerMetadata serverMetadata = ApplicationContext.getServerMetadata();
            JavaMetadata javaMetadata = ApplicationMetadata.getJavaMetadata();
            OperatingSystemMetadata osMetadata = ApplicationMetadata.getOperatingSystemMetadata();

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
                    .append("\n#")//
                    .append("\n#              database : ").append(databaseMetadata.productName)//
                    .append("\n#          database url : ").append(databaseMetadata.url)//
                    .append("\n#         database user : ").append(databaseMetadata.userName)//
                    .append("\n#           driver name : ").append(databaseMetadata.driverName)//
                    .append("\n#        driver version : ").append(databaseMetadata.driverVersion)//
                    .append("\n#")//
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

        private static class JndiConnectionSource implements ConnectionSource {

            private String jndi;

            public JndiConnectionSource(String jndi) {
                this.jndi = jndi;
            }

            @Override
            public boolean isValid() {
                Connection conn = null;
                try {
                    conn = getConnection();
                    return conn.isValid(5);
                } catch (SQLException e) {
                    return false;
                } finally {
                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (SQLException e) {
                        }
                    }
                }
            }

            @Override
            public Connection getConnection() throws SQLException {
                return getDataSource().getConnection();
            }

            private DataSource getDataSource() {
                try {
                    InitialContext ctx = new InitialContext();
                    return (DataSource) ctx.lookup(jndi);
                } catch (Exception e) {
                    return null;
                }
            }

        }
    }
}

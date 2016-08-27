package com.harmony.umbrella.web.context;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.sql.DataSource;

import com.harmony.umbrella.context.ApplicationConfiguration;
import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.metadata.ApplicationMetadata;
import com.harmony.umbrella.context.metadata.DatabaseMetadata;
import com.harmony.umbrella.context.metadata.DatabaseMetadata.ConnectionSource;
import com.harmony.umbrella.context.metadata.JavaMetadata;
import com.harmony.umbrella.context.metadata.OperatingSystemMetadata;
import com.harmony.umbrella.context.metadata.ServerMetadata;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.web.WebApplicationInitializer;

/**
 * 在web应用启动时,伴随开始初始化应用程序
 * 
 * @author wuxii@foxmail.com
 */
public class WebApplicationContextInitializer implements WebApplicationInitializer {

    public static final String INIT_PARAM_DATASOURCE = "datasource";

    public static final String INIT_PARAM_PACKAGES = "packages";

    public static final String INIT_PARAM_DEVMODE = "devMode";

    private static final Log log = Logs.getLog(WebApplicationContextInitializer.class);

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        ApplicationConfiguration appConfig = new ApplicationConfiguration();
        appConfig.withServletContext(servletContext);

        // database connection source
        String jndiName = getInitParam(INIT_PARAM_DATASOURCE, servletContext);
        if (StringUtils.isNotBlank(jndiName)) {
            JndiConnectionSource connectionSource = new JndiConnectionSource(jndiName);
            if (connectionSource.isValid()) {
                appConfig.withConnectionSource(connectionSource);
            } else {
                log.warn("{} connection is not avlid", jndiName);
            }
        } else {
            log.info("unspecified database connection source");
        }

        // application packages
        String[] packages = getInitParams(INIT_PARAM_PACKAGES, ",", servletContext);
        if (packages != null && packages.length > 0) {
            appConfig.withPackages(packages);
        } else {
            log.info("unspecified application package(s)");
        }

        String devMode = getInitParam(INIT_PARAM_DEVMODE, servletContext);
        appConfig.withDevMode(Boolean.valueOf(devMode));

        // init application static metadata information
        ApplicationContext.initStatic(appConfig);

        if (log.isDebugEnabled()) {
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

    private void showApplicationInfo() {

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
                log.error("lookup " + jndi + " failed", e);
                return null;
            }
        }

    }
}
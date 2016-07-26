package com.harmony.umbrella.context;

import java.sql.SQLException;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;

import com.harmony.umbrella.context.metadata.ApplicationClasses;
import com.harmony.umbrella.context.metadata.ApplicationMetadata;
import com.harmony.umbrella.jdbc.ConnectionSource;
import com.harmony.umbrella.jdbc.JndiConnectionSource;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ApplicationInitializer {

    public static final String INIT_PARAM_DATASOURCE = "datasource";

    public static final String INIT_PARAM_PACKAGES = "packages";

    protected static final Log log = Logs.getLog(ApplicationInitializer.class);

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
        if (servletContext != null) {
            ApplicationContext.serverMetadata = ApplicationMetadata.getServerMetadata(servletContext);
        }
    }

    protected void initDatabase(ApplicationConfiguration applicationConfiguration) {
        List<ConnectionSource> connectionSources = applicationConfiguration.getConnectionSources();
        if (connectionSources.isEmpty()) {
            ServletContext servletContext = applicationConfiguration.getServletContext();
            if (servletContext != null) {
                String datasourceJndi = servletContext.getInitParameter(INIT_PARAM_DATASOURCE);
                if (StringUtils.isNotBlank(datasourceJndi)) {
                    ConnectionSource connectionSource = new JndiConnectionSource(datasourceJndi);
                    try {
                        ApplicationContext.databaseMetadata = ApplicationMetadata.getDatabaseMetadata(connectionSource);
                    } catch (SQLException e) {
                        log.error("initial application database information failed", e);
                    }
                }
            }
        }
    }

    protected void initApplicationClasses(ApplicationConfiguration applicationConfiguration) {
        if (ApplicationClasses.isScaned()) {
            log.warn("application scan package before web application setup");
        } else {
            ApplicationClasses.addApplicationPackage(applicationConfiguration.getPackages());
            ApplicationClasses.scan();
        }

    }

    protected void initCustomer(ApplicationConfiguration applicationConfiguration) {

    }

    protected Object lookup(String jndi) {
        try {
            InitialContext ctx = new InitialContext();
            return ctx.lookup(jndi);
        } catch (NamingException e) {
            log.error("lookup " + jndi + " failed", e);
            return null;
        }
    }

}

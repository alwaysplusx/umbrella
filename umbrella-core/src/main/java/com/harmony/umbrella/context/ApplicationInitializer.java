package com.harmony.umbrella.context;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

import com.harmony.umbrella.context.metadata.ApplicationClasses;
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

    protected final ServletContext servletContext;

    public ApplicationInitializer(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public final void init() {
        initServer();

        initDatabase();

        initApplicationClasses();

        initCustomer();
    }

    protected void initServer() {
        ApplicationContext.initialServerMetadata(servletContext);
    }

    protected void initDatabase() {
        String datasourceJndi = getInitParam(INIT_PARAM_DATASOURCE);
        if (StringUtils.isNotBlank(datasourceJndi)) {
            Connection conn = null;
            try {
                DataSource datasource = (DataSource) lookup(datasourceJndi);
                if (datasource != null) {
                    conn = datasource.getConnection();
                    ApplicationContext.initialDatabaseMetadata(conn);
                }
            } catch (SQLException e) {
                log.error("initial application database information failed", e);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                    }
                }
            }
        }
    }

    protected void initApplicationClasses() {
        if (ApplicationClasses.isScaned()) {
            log.warn("application scan package before web application setup");
        } else {
            String[] packages = getInitParams(INIT_PARAM_PACKAGES);
            ApplicationClasses.addApplicationPackage(packages);
            ApplicationClasses.scan();
        }

    }

    protected void initCustomer() {

    }

    protected String getInitParam(String name) {
        return servletContext.getInitParameter(name);
    }

    protected String[] getInitParams(String name) {
        String value = getInitParam(name);
        if (value == null) {
            return new String[0];
        }
        return StringUtils.split(value, ",", true);
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

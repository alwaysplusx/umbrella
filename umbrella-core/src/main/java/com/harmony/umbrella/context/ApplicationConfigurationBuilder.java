package com.harmony.umbrella.context;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

import com.harmony.umbrella.core.ConnectionSource;
import com.harmony.umbrella.util.StringUtils;

public class ApplicationConfigurationBuilder {

    public static final String INIT_PARAM_DATASOURCE = "datasource";

    public static final String INIT_PARAM_PACKAGES = "packages";

    public static final String INIT_PARAM_RUN_MODE = "runMode";

    protected ApplicationConfigurationBuilder() {

    }

    public ApplicationConfiguration build(ServletContext servletContext) {
        ApplicationConfiguration cfg = new ApplicationConfiguration();
        cfg.withServletContext(servletContext);

        // database connection source
        String jndiName = getInitParam(INIT_PARAM_DATASOURCE, servletContext);
        if (StringUtils.isNotBlank(jndiName)) {
            JndiConnectionSource connectionSource = new JndiConnectionSource(jndiName);
            cfg.withConnectionSource(connectionSource);
        } else {
            servletContext.log("unspecified database connection source");
        }

        // application packages
        String[] packages = getInitParams(INIT_PARAM_PACKAGES, ",", servletContext);
        if (packages != null && packages.length > 0) {
            cfg.withPackages(packages);
        } else {
            servletContext.log("unspecified application package(s)");
        }

        String runMode = getInitParam(INIT_PARAM_RUN_MODE, servletContext);
        cfg.withRunMode(runMode);

        return cfg;
    }

    private String getInitParam(String key, ServletContext servletContext) {
        return servletContext != null ? servletContext.getInitParameter(key) : null;
    }

    private String[] getInitParams(String key, String delimiter, ServletContext servletContext) {
        String value = getInitParam(key, servletContext);
        return value != null ? StringUtils.tokenizeToStringArray(value, delimiter) : null;
    }

    protected static class JndiConnectionSource implements ConnectionSource {

        private String jndi;

        public JndiConnectionSource(String jndi) {
            this.jndi = jndi;
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

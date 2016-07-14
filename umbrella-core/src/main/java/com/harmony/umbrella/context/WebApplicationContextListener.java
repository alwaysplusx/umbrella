package com.harmony.umbrella.context;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import com.harmony.umbrella.context.metadata.ApplicationClasses;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.StringUtils;

/**
 * web启动监听, 启动完成后动作如下
 * <p>
 * <ul>
 * <li>初始化web服务器信息
 * <li>初始化应用数据库信息
 * <li>初始化应用的classes信息
 * </ul>
 * 
 * @author wuxii@foxmail.com
 */
public class WebApplicationContextListener implements ServletContextListener {

    public static final String INIT_PARAM_DATASOURCE = "datasource";

    public static final String INIT_PARAM_SCAN_PACKAGE = "scan-package";

    private static final Log log = Logs.getLog(WebApplicationContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();

        ApplicationContext applicationContext = ApplicationContext.getApplicationContext();
        applicationContext.initialServerMetadata(servletContext);

        String dataSourceJndiName = servletContext.getInitParameter(INIT_PARAM_DATASOURCE);
        if (StringUtils.isNotBlank(dataSourceJndiName)) {
            Connection conn = null;
            try {
                DataSource datasource = (DataSource) lookup(dataSourceJndiName);
                if (datasource != null) {
                    conn = datasource.getConnection();
                    applicationContext.initialDatabaseMetadata(conn);
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

        if (ApplicationClasses.isScaned()) {
            log.warn("application scan package before web application setup");
        } else {
            String packages = servletContext.getInitParameter(INIT_PARAM_SCAN_PACKAGE);
            if (StringUtils.isNotBlank(packages)) {
                ApplicationClasses.addApplicationPackage(StringUtils.split(packages, ",", true));
            }
            ApplicationClasses.scan();
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ApplicationContext.getApplicationContext().destroy();
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

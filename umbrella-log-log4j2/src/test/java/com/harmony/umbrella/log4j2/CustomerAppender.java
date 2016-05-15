package com.harmony.umbrella.log4j2;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseAppender;
import org.apache.logging.log4j.core.appender.db.jdbc.ColumnConfig;
import org.apache.logging.log4j.core.appender.db.jdbc.JdbcDatabaseManager;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import com.harmony.umbrella.log.jdbc.ConnectionSource;

/**
 * log4j2.xml configuration attribute packages
 * @author wuxii@foxmail.com
 */
@Plugin(name = "Customer", category = "Core", elementType = "appender", printObject = true)
public class CustomerAppender extends AbstractDatabaseAppender<JdbcDatabaseManager> {

    protected CustomerAppender(String name, Filter filter, boolean ignoreExceptions, JdbcDatabaseManager manager) {
        super(name, filter, ignoreExceptions, manager);
    }

    private static final long serialVersionUID = 1L;

    @PluginFactory
    public static CustomerAppender createAppender(@PluginAttribute("name") final String name,//
            @PluginAttribute("tableName") final String tableName,//
            @PluginAttribute("bufferSize") final String bufferSize,//
            @PluginElement("Filter") final Filter filter,//
            @PluginElement("ConnectionSource") final ConnectionSource connectionSource,//
            @PluginElement("Columns") final ColumnConfig[] columnConfigs) {
        // return new CustomerAppender(name, filter, null);
        return null;
    }
}

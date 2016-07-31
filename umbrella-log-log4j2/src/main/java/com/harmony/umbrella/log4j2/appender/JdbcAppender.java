package com.harmony.umbrella.log4j2.appender;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseAppender;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseManager;
import org.apache.logging.log4j.core.appender.db.jdbc.ConnectionSource;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.Booleans;

import com.harmony.umbrella.log.db.jdbc.Column;

/**
 * @author wuxii@foxmail.com
 */
@Plugin(name = "JDBC-UMBRELLA", category = "Core", elementType = "appender", printObject = true)
public class JdbcAppender extends AbstractDatabaseAppender<AbstractDatabaseManager> {

    private static final long serialVersionUID = 1L;

    protected JdbcAppender(String name, Filter filter, boolean ignoreExceptions, AbstractDatabaseManager manager) {
        super(name, filter, ignoreExceptions, manager);
    }

    @PluginFactory
    public static JdbcAppender createAppender(@PluginAttribute("name") final String name, //
            @PluginAttribute("ignoreExceptions") final String ignore, //
            @PluginElement("Filter") final Filter filter, //
            @PluginElement("ConnectionSource") final ConnectionSource connectionSource, //
            @PluginAttribute("bufferSize") final String bufferSize, //
            @PluginAttribute("tableName") final String tableName, //
            @PluginElement("Columns") final Column[] columns) {

        final int bufferSizeInt = AbstractAppender.parseInt(bufferSize, 0);
        final boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);

        return new JdbcAppender(name, filter, ignoreExceptions, new AbstractDatabaseManager(tableName, bufferSizeInt) {

            @Override
            protected void startupInternal() throws Exception {
            }

            @Override
            protected void shutdownInternal() throws Exception {
            }

            @Override
            protected void connectAndStart() {
            }

            @Override
            protected void writeInternal(LogEvent event) {
                System.out.println(event.getMessage());
            }

            @Override
            protected void commitAndClose() {
            }

        });
    }

}

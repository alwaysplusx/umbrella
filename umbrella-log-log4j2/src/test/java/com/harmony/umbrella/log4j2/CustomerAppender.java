package com.harmony.umbrella.log4j2;

import java.io.Serializable;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.appender.OutputStreamManager;
import org.apache.logging.log4j.core.appender.db.jdbc.ColumnConfig;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import com.harmony.umbrella.sql.ConnectionSource;

/**
 * log4j2.xml configuration attribute packages
 * 
 * @author wuxii@foxmail.com
 */
@Plugin(name = "Customer", category = "Core", elementType = "appender", printObject = true)
public class CustomerAppender extends AbstractOutputStreamAppender<OutputStreamManager> {

    protected CustomerAppender(String name, Layout<? extends Serializable> layout, Filter filter, boolean ignoreExceptions, boolean immediateFlush,
            OutputStreamManager manager) {
        super(name, layout, filter, ignoreExceptions, immediateFlush, manager);
    }

    private static final long serialVersionUID = 1L;

    @PluginFactory
    public static CustomerAppender createAppender(@PluginAttribute("name") final String name, //
            @PluginAttribute("tableName") final String tableName, //
            @PluginAttribute("bufferSize") final String bufferSize, //
            @PluginElement("Filter") final Filter filter, //
            @PluginElement("ConnectionSource") final ConnectionSource connectionSource, //
            @PluginElement("Columns") final ColumnConfig[] columnConfigs) {
        return null;
    }
}

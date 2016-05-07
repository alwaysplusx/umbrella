package com.harmony.umbrella.log.jdbc;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

/**
 * @author wuxii@foxmail.com
 */
@Plugin(name = "Field", category = "Core", printObject = true)
public class FieldColumnConfig {

    public FieldColumnConfig(Configuration config, String field, String column) {
    }

    @PluginFactory
    public static FieldColumnConfig createFieldColumnConfig(//
            @PluginConfiguration Configuration config, //
            @PluginAttribute("filed") final String field,//
            @PluginAttribute("column") final String column) {
        return new FieldColumnConfig(config, field, column);
    }

}

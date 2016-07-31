package com.harmony.umbrella.log4j2.db;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.w3c.dom.Element;

import com.harmony.umbrella.log.db.jdbc.Column;

/**
 * @author wuxii@foxmail.com
 */
@Plugin(name = "Columns", category = "Core", printObject = true)
public class Columns {

    @PluginFactory
    public static Column createColumn(@PluginElement("column") final Element column) {
        return null;
    }
}

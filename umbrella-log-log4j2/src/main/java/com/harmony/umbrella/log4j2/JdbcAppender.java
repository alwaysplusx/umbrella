//package com.harmony.umbrella.log4j2;
//
//import org.apache.logging.log4j.core.Filter;
//import org.apache.logging.log4j.core.appender.AbstractAppender;
//import org.apache.logging.log4j.core.appender.db.AbstractDatabaseAppender;
//import org.apache.logging.log4j.core.appender.db.jdbc.ColumnConfig;
//import org.apache.logging.log4j.core.appender.db.jdbc.ConnectionSource;
//import org.apache.logging.log4j.core.appender.db.jdbc.JdbcDatabaseManager;
//import org.apache.logging.log4j.core.config.plugins.Plugin;
//import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
//import org.apache.logging.log4j.core.config.plugins.PluginElement;
//import org.apache.logging.log4j.core.config.plugins.PluginFactory;
//import org.apache.logging.log4j.core.util.Booleans;
//
///**
// * @author wuxii@foxmail.com
// */
//@Plugin(name = "JDBC", category = "UMBRELLA", elementType = "appender", printObject = true)
//public class JdbcAppender extends AbstractDatabaseAppender<JdbcDatabaseManager> {
//
//    private static final long serialVersionUID = 1L;
//
//    protected JdbcAppender(String name, Filter filter, boolean ignoreExceptions, JdbcDatabaseManager manager) {
//        super(name, filter, ignoreExceptions, manager);
//    }
//
//    @PluginFactory
//    public static JdbcAppender createAppender(@PluginAttribute("name") final String name,//
//            @PluginAttribute("ignoreExceptions") final String ignore, //
//            @PluginElement("Filter") final Filter filter,//
//            @PluginElement("ConnectionSource") final ConnectionSource connectionSource,//
//            @PluginAttribute("bufferSize") final String bufferSize,//
//            @PluginAttribute("tableName") final String tableName, //
//            @PluginElement("ColumnConfigs") final ColumnConfig[] columnConfigs) {
//
//        final int bufferSizeInt = AbstractAppender.parseInt(bufferSize, 0);
//        final boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
//
//        final StringBuilder managerName = new StringBuilder("jdbcManager{ description=").append(name).append(", bufferSize=").append(bufferSizeInt)
//                .append(", connectionSource=").append(connectionSource.toString()).append(", tableName=").append(tableName).append(", columns=[ ");
//
//        int i = 0;
//        for (final ColumnConfig column : columnConfigs) {
//            if (i++ > 0) {
//                managerName.append(", ");
//            }
//            managerName.append(column.toString());
//        }
//
//        managerName.append(" ] }");
//
//        final JdbcDatabaseManager manager = JdbcDatabaseManager.getJDBCDatabaseManager(managerName.toString(), bufferSizeInt, connectionSource, tableName,
//                columnConfigs);
//        if (manager == null) {
//            return null;
//        }
//
//        return new JdbcAppender(name, filter, ignoreExceptions, manager);
//    }
//}

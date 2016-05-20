//package com.harmony.umbrella.log4j.jdbc;
//
//import java.sql.Connection;
//import java.sql.DatabaseMetaData;
//import java.sql.SQLException;
//import java.util.Properties;
//
//import org.apache.log4j.AppenderSkeleton;
//import org.apache.log4j.spi.LoggingEvent;
//import org.apache.log4j.xml.UnrecognizedElementHandler;
//import org.w3c.dom.Element;
//
//import com.harmony.umbrella.log.jdbc.ConnectionSource;
//import com.harmony.umbrella.log.jdbc.JdbcConnectionSource;
//import com.harmony.umbrella.log.jdbc.JndiConnectionSource;
//
///**
// * @author wuxii@foxmail.com
// */
//public class MyJdbcAppender extends AppenderSkeleton implements UnrecognizedElementHandler {
//
//    private ConnectionSource connectionSource;
//
//    private String tableName;
//    private int bufferSize = 1;
//    private boolean upperCase = true;
//
//    private String jndiName;
//    private String url;
//    private String user;
//    private String password;
//
//    private boolean initialize;
//
//    protected void init() {
//        if (!initialize) {
//
//            initConnectionSource();
//
//            initColumns();
//
//            initDatabaseManager();
//
//            initialize = true;
//        }
//    }
//
//    private void initConnectionSource() {
//        if (jndiName != null) {
//            this.connectionSource = new JndiConnectionSource(jndiName);
//        } else if (url != null && user != null && password != null) {
//            this.connectionSource = new JdbcConnectionSource(url, user, password);
//        }
//    }
//
//    private void initColumns() {
//        try {
//            Connection conn = connectionSource.getConnection();
//        } catch (SQLException e) {
//        }
//
//    }
//
//    private void initDatabaseManager() {
//    }
//
//    @Override
//    protected void append(LoggingEvent event) {
//        init();
//    }
//
//    @Override
//    public void close() {
//    }
//
//    @Override
//    public boolean requiresLayout() {
//        return false;
//    }
//
//    @Override
//    public boolean parseUnrecognizedElement(Element element, Properties props) throws Exception {
//        return false;
//    }
//
//    public void setTableName(String tableName) {
//        this.tableName = tableName;
//    }
//
//    public void setBufferSize(int bufferSize) {
//        this.bufferSize = bufferSize;
//    }
//
//    public void setUpperCase(boolean upperCase) {
//        this.upperCase = upperCase;
//    }
//
//    public void setInitialize(boolean initialize) {
//        this.initialize = initialize;
//    }
//
//    public void setJndiName(String jndiName) {
//        this.jndiName = jndiName;
//    }
//
//    public void setUrl(String url) {
//        this.url = url;
//    }
//
//    public void setUser(String user) {
//        this.user = user;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//}

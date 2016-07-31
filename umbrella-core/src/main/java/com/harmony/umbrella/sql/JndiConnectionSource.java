package com.harmony.umbrella.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * @author wuxii@foxmail.com
 */
public class JndiConnectionSource implements ConnectionSource {

    private String jndiName;
    private final Properties props = new Properties();

    private DataSource dataSource;
    private int timeout;

    public JndiConnectionSource() {
    }

    public JndiConnectionSource(String jndiName) {
        this(jndiName, new Properties());
    }

    public JndiConnectionSource(String jndiName, Properties props) {
        this.jndiName = jndiName;
        this.props.putAll(props);
    }

    @Override
    public boolean isValid() {
        Connection conn = null;
        try {
            conn = getConnection();
            return conn.isValid(timeout);
        } catch (SQLException e) {
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
        return false;
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            return getDataSource().getConnection();
        } catch (NamingException e) {
            throw new SQLException("jndi not find", e);
        }
    }

    private DataSource getDataSource() throws NamingException {
        if (dataSource == null) {
            InitialContext ctx = new InitialContext(props);
            Object obj = ctx.lookup(this.jndiName);
            if (obj instanceof DataSource) {
                dataSource = (DataSource) obj;
            }
        }
        return dataSource;
    }

    public String getJndiName() {
        return jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

}

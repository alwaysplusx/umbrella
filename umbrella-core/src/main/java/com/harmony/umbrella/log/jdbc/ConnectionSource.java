package com.harmony.umbrella.log.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author wuxii@foxmail.com
 */
public interface ConnectionSource {

    Connection getConnection() throws SQLException;

}

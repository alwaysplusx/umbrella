package com.harmony.umbrella.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author wuxii@foxmail.com
 */
public interface ConnectionSource {

    boolean isValid();

    Connection getConnection() throws SQLException;

}

package com.harmony.umbrella.core;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 
 * @author wuxii@foxmail.com
 */
public interface ConnectionSource {

    Connection getConnection() throws SQLException;

}
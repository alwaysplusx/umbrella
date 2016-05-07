package com.harmony.umbrella.log;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import org.h2.jdbcx.JdbcConnectionPool;

import com.harmony.umbrella.util.IOUtils;

/**
 * @author wuxii@foxmail.com
 */
public class LogPrinter {

    static String message;

    static {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("message");
        try {
            message = IOUtils.toString(inputStream);
        } catch (IOException e) {
        }
    }

    static JdbcConnectionPool ds = JdbcConnectionPool.create("jdbc:h2:file:~/.h2/harmony/log", "sa", "");

    private static final Log log = Logs.getLog(LogPrinter.class);

    public static void main(String[] args) throws InterruptedException {

        LogMessage msg = LogMessage.create(log)//
                .bizId(1L)//
                .start();

        Thread.sleep(1000);

        msg.finish().bizModule("Object")//
                .operator("wuxii")//
                .operatorId("1")//
                .action("保存")//
                .module("Sample")//
                .level(Level.INFO)//
                .currentStack()//
                .currentThread()//
                .message("some text").log();
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}

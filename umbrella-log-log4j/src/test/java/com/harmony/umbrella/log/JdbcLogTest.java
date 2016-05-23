package com.harmony.umbrella.log;

import org.junit.Test;

import com.harmony.umbrella.log.annotation.Logging.LogType;
import com.harmony.umbrella.log4j.StaticLogger;

/**
 * @author wuxii@foxmail.com
 */
public class JdbcLogTest {

    private static final Log log = Logs.getLog(JdbcLogTest.class);

    @Test
    public void testPrintLog() {
        StaticLogger.level = 0;
        LogMessage.create(log)//
                .level(Level.INFO)//
                .module("系统模块")//
                .key("http://www.baidu.com")//
                .action("修改系统资源链接")//
                .start(System.currentTimeMillis())//
                .message("修改系统资源链接{}, 输入条件为:{}", "http://localhost:8080", "9090")//
                .result("success")//
                .finish(System.currentTimeMillis() + 1000)//
                .operatorName("wuxii")//
                .operatorId(1l)//
                .operatorHost("localhost")//
                .currentStack()//
                .type(LogType.OPERATION)//
                .currentStack()//
                .put("name", "wuxii")//
                .threadName(Thread.currentThread().getName())//
                .exception(null)//
                .log();
    }

}

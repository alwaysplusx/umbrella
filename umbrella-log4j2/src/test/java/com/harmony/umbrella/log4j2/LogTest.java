package com.harmony.umbrella.log4j2;

import org.junit.Test;

import com.harmony.umbrella.log.Level;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.LogMessage;
import com.harmony.umbrella.log.Logs;

/**
 * @author wuxii@foxmail.com
 */
public class LogTest {

    private static final Log log = Logs.getLog(LogTest.class);

    public static void main(String[] args) {
        log.info("test message");
    }

    @Test
    public void testLog() {
        LogMessage logMessage = LogMessage.create(log)//
                .start()//
                .level(Level.INFO)//
                .key("1")//
                .module("测试模块")//
                .action("测试日志打印")//
                .clientId("localhost")//
                .userId(1l)//
                .username("wuxii")//
                .message("用户[{}]对日志进行打印操作, 打印的日志信息为[{}]", "wuxii", "Hello World!")//
                .result("success")//
                .currentStack()//
                .currentThread()//
                .put("name", "wuxii")//
                .finish();//

        logMessage.log();
    }
}

package com.harmony.umbrella.log;

import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
public class LogTest {

    private static final Log log = Logs.getLog(LogTest.class);

    public static void main(String[] args) {
        log.info("test log, a={}", "a");
    }

    @Test
    public void testLog() {
        LogMessage logMessage = LogMessage.create(log)//
                .start()//
                .level(Level.INFO)//
                .key("1")//
                .module("测试模块")//
                .action("测试日志打印")//
                .userHost("localhost")//
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

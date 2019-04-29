package com.harmony.umbrella.log;

import lombok.extern.slf4j.Slf4j;

/**
 * @author wuxii
 */
@Slf4j
public class LogMessageTest {

    public static void main(String[] args) {
        new LogMessage()
                .start()
                .level(Level.INFO)
                .key("1")
                .module("测试模块")
                .action("测试日志打印")
                .userId(1L)
                .username("wuxii")
                .message("用户[{}]对日志进行打印操作, 打印的日志信息为[{}]", "wuxii", "Hello World!")//
                .currentThreadFrame()
                .currentThread()
                .finish()
                .log(log);
        log.info("Hello {}!", "world");
    }

}

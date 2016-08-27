package com.harmony.umbrella.log;

/**
 * @author wuxii@foxmail.com
 */
public class LogTest {

    private static final Log log = Logs.getLog(LogTest.class);

    public static void main(String[] args) {
        log.info("test log, a={}", "a");
    }

}

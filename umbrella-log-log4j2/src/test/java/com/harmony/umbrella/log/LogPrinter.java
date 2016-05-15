package com.harmony.umbrella.log;

/**
 * @author wuxii@foxmail.com
 */
public class LogPrinter {

    private static final Log log = Logs.getLog(LogPrinter.class);

    public static void main(String[] args) {
        log.info("hi");
    }

}

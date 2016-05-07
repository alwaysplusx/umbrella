package com.harmony.umbrella.log;

/**
 * @author wuxii@foxmail.com
 */
public class LogPrinter {

    private static final Log log = Logs.getLog(LogPrinter.class);

    static String message;

    static {
    }

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

}

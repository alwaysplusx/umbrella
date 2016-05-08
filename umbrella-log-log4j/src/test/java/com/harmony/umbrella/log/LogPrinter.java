package com.harmony.umbrella.log;

/**
 * @author wuxii@foxmail.com
 */
public class LogPrinter {

    private static final Log log = Logs.getLog(LogPrinter.class);

    public static void main(String[] args) throws InterruptedException {

        LogMessage msg = LogMessage.create(log)//
                .id(1L)//
                .start();

        Thread.sleep(1000);

        msg.finish().operator("wuxii")//
                .module("Sample")//
                .operatorId("1")//
                .action("保存")//
                .level(Level.INFO)//
                .currentStack()//
                .currentThread()//
                .message("some text").log();
    }

}

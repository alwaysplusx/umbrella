package com.harmony.umbrella.log;

import com.harmony.umbrella.log4j.StaticLogger;

/**
 * @author wuxii@foxmail.com
 */
public class LogPrinter {

    private static final Log log = Logs.getLog(LogPrinter.class);

    public static void main(String[] args) throws InterruptedException {

        StaticLogger.level = 2;
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
                .currentStack()//
                .threadName(Thread.currentThread().getName())//
                .exception(null)//
                .log();
    }

}

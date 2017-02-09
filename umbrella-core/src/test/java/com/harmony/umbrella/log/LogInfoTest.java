package com.harmony.umbrella.log;

/**
 * @author wuxii@foxmail.com
 */
public class LogInfoTest {

    private static final Log log = Logs.getLog(LogInfoTest.class);

    public static void main(String[] args) {
        LogMessage logMessage = LogMessage.create(log)//
                .start()//
                .level(Level.INFO)//
                .key("1")//
                .module("测试模块")//
                .action("测试日志打印")//
                .operatorHost("localhost")//
                .operatorId("1")//
                .operatorName("wuxii")//
                .message("用户[{}]对日志进行打印操作, 打印的日志信息为[{}]", "wuxii", "Hello World!")//
                .result("success")//
                .currentStack()//
                .currentThread()//
                .put("name", "wuxii")//
                .finish();//

        LogInfo info = logMessage.asInfo();

        System.out.println(info);

        logMessage.log();
    }

}

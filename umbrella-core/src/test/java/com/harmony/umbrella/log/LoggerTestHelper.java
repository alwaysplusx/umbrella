package com.harmony.umbrella.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Test;

import com.harmony.umbrella.log.log4j2.LogWriterAppender;
import com.harmony.umbrella.log.support.LogWriter;

/**
 * @author wuxii@foxmail.com
 */
public class LoggerTestHelper {

    @Test
    public void testLog() {
        testWithLog(Logs.getLog());
    }

    @Test
    public void testLog4J2() {
        // org.apache.logging.log4j.message.ReusableMessageFactory +
        // org.apache.logging.log4j.core.impl.ReusableLogEventFactory的组合无法传递objectMessage

        System.setProperty("log4j2.messageFactory", "org.apache.logging.log4j.message.ParameterizedMessageFactory");
        Configurator.setAllLevels("com.harmony", org.apache.logging.log4j.Level.INFO);
        Configurator.setRootLevel(org.apache.logging.log4j.Level.INFO);

        LoggerContext ctx = ((LoggerContext) LogManager.getContext(false));
        Configuration cfg = ctx.getConfiguration();
        Logger rootLogger = ctx.getRootLogger();
        cfg.addLoggerAppender(rootLogger, new LogWriterAppender(new SystemOutLogWriter()));
        ctx.updateLoggers();

        // Collection<Appender> values = config.getAppenders().values();
        // for (Appender appender : values) {
        // System.out.println(appender);
        // }

        LogManager.getLogger(LoggerTestHelper.class).info(">>>>>>>>>>");
        testLog();
    }

    private void testWithLog(Log log) {
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

    public static void print() {
        LogMessage logMessage = LogMessage.create(Logs.getLog())//
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

    static final class SystemOutLogWriter implements LogWriter {

        @Override
        public void startup() {
        }

        @Override
        public void write(LogInfo info) {
            System.err.println(info.getMessage());
        }

        @Override
        public void flush() {
        }

        @Override
        public void shutdown() {
        }

    }

}

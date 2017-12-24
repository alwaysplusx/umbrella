package com.harmony.umbrella.log;

/**
 * @author wuxii@foxmail.com
 */
public class StaticLogger {

    private static final LogProvider staticLoggerProvider = new Logs.SystemLogProvider();

    private static final Log log = staticLoggerProvider.getLogger(StaticLogger.class.getName());

    public static Log getStaticLogger(Class<?> clazz) {
        return getStaticLogger(clazz.getName());
    }

    public static Log getStaticLogger(String name) {
        return staticLoggerProvider.getLogger(name);
    }

    public static void trace(String text, Object... args) {
        log.trace(text, args);
    }

    public static void debug(String text, Object... args) {
        log.debug(text, args);
    }

    public static void info(String text, Object... args) {
        log.info(text, args);
    }

    public static void warn(String text, Object... args) {
        log.warn(text, args);
    }

    public static void error(String text, Object... args) {
        log.error(text, args);
    }

}

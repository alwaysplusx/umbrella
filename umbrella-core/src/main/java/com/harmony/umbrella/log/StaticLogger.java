package com.harmony.umbrella.log;

/**
 * @author wuxii@foxmail.com
 */
public class StaticLogger {

    public static void trace(String text, Object... args) {
        log().trace(text, args);
    }

    public static void debug(String text, Object... args) {
        log().debug(text, args);
    }

    public static void info(String text, Object... args) {
        log().info(text, args);
    }

    public static void warn(String text, Object... args) {
        log().warn(text, args);
    }

    public static void error(String text, Object... args) {
        log().error(text, args);
    }

    public static Log getStaticLogger(Class<?> clazz) {
        return getStaticLogger(clazz.getName());
    }

    public static Log getStaticLogger(String name) {
        return new Logs.SystemLogProvider().getLogger(name);
    }

    private static Log log() {
        return getStaticLogger(StaticLogger.class);
    }

}

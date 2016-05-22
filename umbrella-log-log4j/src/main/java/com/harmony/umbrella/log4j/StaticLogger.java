package com.harmony.umbrella.log4j;

/**
 * @author wuxii@foxmail.com
 */
public class StaticLogger {

    // trace info
    // 0     1
    public static int level = -1;

    public static void info(String text) {
        if (level >= 1) {
            System.out.println("log4j:" + text);
        }
    }

    public static void warn(String text) {
        System.err.println("log4j:" + text);
    }

    public static void debug(String text) {
        if (level > 0) {
            System.out.println("log4j:" + text);
        }
    }
}

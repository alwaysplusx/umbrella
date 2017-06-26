package com.harmony.umbrella.log;

import java.io.PrintStream;

/**
 * @author wuxii@foxmail.com
 */
public class StaticLogger {

    // trace info
    // 0 1
    public static int level = -1;

    private static final PrintStream out = System.out;
    private static final PrintStream err = System.err;

    public static void trace(String text, Object... args) {
        if (level >= 4) {
            out.println("trace: " + format(text, args));
        }
    }

    public static void debug(String text, Object... args) {
        if (level >= 2) {
            out.println("debug: " + format(text, args));
        }
    }

    public static void info(String text, Object... args) {
        if (level >= 3) {
            out.println("info: " + format(text, args));
        }
    }

    public static void warn(String text, Object... args) {
        if (level >= 1) {
            err.println("warn: " + format(text, args));
        }
    }

    public static void error(String text, Object... args) {
        if (level >= 0) {
            err.println("error: " + format(text, args));
        }
    }

    private static String format(String text, Object... args) {
        return text;
    }
}

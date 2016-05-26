package com.harmony.umbrella.util;

import com.harmony.umbrella.context.metadata.ApplicationMetadata;

/**
 * @author wuxii@foxmail.com
 */
public final class Environments {

    /**
     * Constant identifying the 1.3.x JVM (JDK 1.3).
     */
    public static final int JAVA_13 = 0;

    /**
     * Constant identifying the 1.4.x JVM (J2SE 1.4).
     */
    public static final int JAVA_14 = 1;

    /**
     * Constant identifying the 1.5 JVM (Java 5).
     */
    public static final int JAVA_15 = 2;

    /**
     * Constant identifying the 1.6 JVM (Java 6).
     */
    public static final int JAVA_16 = 3;

    /**
     * Constant identifying the 1.7 JVM (Java 7).
     */
    public static final int JAVA_17 = 4;

    /**
     * Constant identifying the 1.8 JVM (Java 8).
     */
    public static final int JAVA_18 = 5;

    /**
     * Constant identifying the 1.9 JVM (Java 9).
     */
    public static final int JAVA_19 = 6;

    /**
     * java版本
     */
    private static final String javaVersion = ApplicationMetadata.getJavaMetadata().javaVersion;

    /**
     * 操作系统名称
     */
    private static final String operatingSystemName = ApplicationMetadata.getOperatingSystemMetadata().osName;

    /**
     * java大版本号
     */
    private static final int majorJavaVersion;

    static {
        // version String should look like "1.4.2_10"
        if (javaVersion.contains("1.9.")) {
            majorJavaVersion = JAVA_19;
        } else if (javaVersion.contains("1.8.")) {
            majorJavaVersion = JAVA_18;
        } else if (javaVersion.contains("1.7.")) {
            majorJavaVersion = JAVA_17;
        } else {
            // else leave 1.6 as default (it's either 1.6 or unknown)
            majorJavaVersion = JAVA_16;
        }
    }

    public static boolean isWindows() {
        return operatingSystemName.toLowerCase().contains("windows");
    }

    public static boolean isUnix() {
        return operatingSystemName.toLowerCase().contains("unix");
    }

    public static boolean isLinux() {
        return operatingSystemName.toLowerCase().contains("linux");
    }

    public static String getProperty(String key) {
        return getProperty(key, null);
    }

    public static String getProperty(String key, String defaultValue) {
        return System.getProperty(key, defaultValue);
    }

    public static boolean isJDK6() {
        return majorJavaVersion == JAVA_16;
    }

    public static boolean isJDK7() {
        return majorJavaVersion == JAVA_17;
    }

    public static boolean isJDK8() {
        return majorJavaVersion == JAVA_18;
    }

    public static boolean isJDK9() {
        return majorJavaVersion == JAVA_19;
    }

    /**
     * Return the full Java version string, as returned by
     * {@code System.getProperty("java.version")}.
     * 
     * @return the full Java version string
     * @see System#getProperty(String)
     */
    public static String getJavaVersion() {
        return javaVersion;
    }

    /**
     * Get the major version code. This means we can do things like
     * {@code if (getMajorJavaVersion() >= JAVA_17)}.
     * 
     * @return a code comparable to the {@code JAVA_XX} codes in this class
     * @see #JAVA_16
     * @see #JAVA_17
     * @see #JAVA_18
     * @see #JAVA_19
     */
    public static int getMajorJavaVersion() {
        return majorJavaVersion;
    }

}

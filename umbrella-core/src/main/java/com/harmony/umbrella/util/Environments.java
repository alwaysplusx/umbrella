package com.harmony.umbrella.util;

import com.harmony.umbrella.context.ApplicationMetadata;
import com.harmony.umbrella.context.ApplicationMetadata.JVMInformation;
import com.harmony.umbrella.context.ApplicationMetadata.OSInformation;

/**
 * @author wuxii@foxmail.com
 */
public final class Environments {

    public static final JVMInformation JVM_INFORMATION = ApplicationMetadata.JVMINFO;

    public static final OSInformation OS_INFORMATION = ApplicationMetadata.OSINFO;

    public static boolean isWindows() {
        return OS_INFORMATION.osName.toLowerCase().contains("windows");
    }

    public static boolean isUnix() {
        return false;
    }

    public static boolean isLinux() {
        return false;
    }

    public static String getProperty(String key, String defaultValue) {
        return System.getProperty(key, defaultValue);
    }

}

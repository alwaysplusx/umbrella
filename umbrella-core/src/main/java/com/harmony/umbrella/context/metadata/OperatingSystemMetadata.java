package com.harmony.umbrella.context.metadata;

/**
 * 操作系统信息
 * 
 * @author wuxii@foxmail.com
 */
public final class OperatingSystemMetadata {

    public final String osName;
    public final String osVersion;
    public final String osPatchLevel;
    public final String userHome;
    public final String userName;
    public final String language;
    public final String timeZone;
    public final String fileEncoding;
    public final String fileSeparator;
    public final String cpu;
    public final String cpuModel;

    OperatingSystemMetadata() {
        this.osName = getSystemProperty("os.name");
        this.osVersion = getSystemProperty("os.version");
        this.osPatchLevel = getSystemProperty("sun.os.patch.level");
        this.userHome = getSystemProperty("user.home");
        this.userName = getSystemProperty("user.name");
        this.language = getSystemProperty("user.language");
        this.timeZone = getSystemProperty("user.timezone");
        this.fileEncoding = getSystemProperty("file.encoding");
        this.fileSeparator = getSystemProperty("file.separator");
        this.cpu = getSystemProperty("sun.cpu.isalist");
        this.cpuModel = getSystemProperty("sun.arch.data.model");
    }

    private static String getSystemProperty(String key) {
        return System.getProperty(key);
    }
}
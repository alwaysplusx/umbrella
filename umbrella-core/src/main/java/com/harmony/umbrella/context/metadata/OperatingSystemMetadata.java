package com.harmony.umbrella.context.metadata;

import static java.lang.System.getProperty;

/**
 * 操作系统信息
 *
 * @author wuxii@foxmail.com
 */
public final class OperatingSystemMetadata {

    public static final OperatingSystemMetadata INSTANCE = new OperatingSystemMetadata();

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

    private OperatingSystemMetadata() {
        this.osName = getProperty("os.name");
        this.osVersion = getProperty("os.version");
        this.osPatchLevel = getProperty("sun.os.patch.level");
        this.userHome = getProperty("user.home");
        this.userName = getProperty("user.name");
        this.language = getProperty("user.language");
        this.timeZone = getProperty("user.timezone");
        this.fileEncoding = getProperty("file.encoding");
        this.fileSeparator = getProperty("file.separator");
        this.cpu = getProperty("sun.cpu.isalist");
        this.cpuModel = getProperty("sun.arch.data.model");
    }

}
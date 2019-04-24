package com.harmony.umbrella.context.metadata;

import static java.lang.System.getProperty;

/**
 * 应用所使用的jvm属性
 *
 * @author wuxii@foxmail.com
 */
public final class JavaMetadata {

    public static final JavaMetadata INSTANCE = new JavaMetadata();

    public final String specificationName;
    public final String specificationVersion;
    public final String runtimeName;
    public final String runtimeVersion;
    public final String vmName;
    public final String vmVersion;
    public final String vmVendor;
    public final String classVersion;
    public final String libraryPath;
    public final String classPath;
    public final String javaVersion;
    public final String javaHome;
    public final String javaVendor;

    private JavaMetadata() {
        this.specificationName = getProperty("java.specification.name");
        this.specificationVersion = getProperty("java.specification.version");
        this.runtimeName = getProperty("java.runtime.name");
        this.runtimeVersion = getProperty("java.runtime.version");
        this.vmName = getProperty("java.vm.name");
        this.vmVendor = getProperty("java.vm.vendor");
        this.vmVersion = getProperty("java.vm.version");
        this.javaVersion = getProperty("java.version");
        this.javaHome = getProperty("java.home");
        this.javaVendor = getProperty("java.vendor");
        this.classVersion = getProperty("java.class.version");
        this.libraryPath = getProperty("java.library.path");
        this.classPath = getProperty("java.class.path");
    }

}
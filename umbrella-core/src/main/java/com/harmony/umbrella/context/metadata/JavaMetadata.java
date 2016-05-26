package com.harmony.umbrella.context.metadata;

/**
 * 应用所使用的jvm属性
 * 
 * @author wuxii@foxmail.com
 */
public final class JavaMetadata {

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

    JavaMetadata() {
        this.specificationName = getSystemProperty("java.specification.name");
        this.specificationVersion = getSystemProperty("java.specification.version");
        this.runtimeName = getSystemProperty("java.runtime.name");
        this.runtimeVersion = getSystemProperty("java.runtime.version");
        this.vmName = getSystemProperty("java.vm.name");
        this.vmVendor = getSystemProperty("java.vm.vendor");
        this.vmVersion = getSystemProperty("java.vm.version");
        this.javaVersion = getSystemProperty("java.version");
        this.javaHome = getSystemProperty("java.home");
        this.javaVendor = getSystemProperty("java.vendor");
        this.classVersion = getSystemProperty("java.class.version");
        this.libraryPath = getSystemProperty("java.library.path");
        this.classPath = getSystemProperty("java.class.path");
    }

    private static String getSystemProperty(String key) {
        return System.getProperty(key);
    }

}
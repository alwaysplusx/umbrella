package com.harmony.umbrella.context;

/**
 * @author wuxii@foxmail.com
 */
public abstract class WebXmlConstant {

    // for ApplicationConfigurationBuilder

    public static final String CONTEXT_PARAM_DATASOURCE = "harmony.cfg.datasource";

    public static final String CONTEXT_PARAM_SCAN_PACKAGES = "harmony.cfg.scan-packages";

    public static final String CONTEXT_PARAM_INITIALIZER = "harmony.cfg.initializer";

    public static final String CONTEXT_PARAM_SHUTDOWN_HOOKS = "harmony.cfg.shutdown-hooks";

    // for ApplicationServletContainerInitializer

    public static final String CONTEXT_PARAM_SERVLET_AUTOWIRE = "harmony.servlet.autowire";

    public static final String CONTEXT_PARAM_BUILDER = "harmony.servlet.config-builder";

    public static final String CONTEXT_PARAM_SHOW_INFO = "harmony.servlet.show-info";

    public static final String CONTEXT_PARAM_SCAN_HANDLES_TYPES = "harmony.servlet.scan-handlers-types";

    public static final String CONTEXT_PARAM_SCAN_PACKAGES_VALUE = "com.harmony";

    public static final String CONTEXT_PARAM_DATASOURCE_VALUE = "jdbc/harmony";

    public static final String CONTEXT_PARAM_SCAN_INIT = "harmony.servlet.scan-init";

    public static final String CONTEXT_PARAM_SCAN_ASYNC = "harmony.servlet.scan-async";

    public static final String CONTEXT_ATTRIBUTE_APP_CONFIG = ApplicationConfiguration.class.getName() + ".app-config";

    private WebXmlConstant() {
    }

}

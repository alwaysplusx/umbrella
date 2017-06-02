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

    /*
     * harmony.cfg(ApplicationConfiguration)
     *  keys: 
     *      datasource: 数据源的jndi/beanName
     *      scan-packages: 应用扫描的包(包括子包)
     *      initializer:  应用自定义初始化器
     *      shutdown-hooks: 关闭时候调用的钩子className/beanName
     *      properties: 扩展配置
     *      
     * harmony.cfg.properties(Map)
     *  keys: 
     *      scan-init: 扫描class的时候是否对其进行初始化
     *      scan-async: 是否开启异步扫描
     *      scan-handlers-types: 是否自行检查对应的HandlersTypes
     *      show-info: 是否在初始化application后显示对应的应用信息
     *      listener-autowire: 是否对applicationListener进行自动注入
     *      hook-autowire: 是否对shutdownHooks进行自动注入
     *      custom-builder(TBD): 自定义applicationConfigurationBuilder
     */

    // for ApplicationServletContainerInitializer

    public static final String CONTEXT_PARAM_SCAN_PACKAGES_VALUE = "com.harmony";

    public static final String CONTEXT_PARAM_DATASOURCE_VALUE = "jdbc/harmony";

    public static final String CONTEXT_PARAM_SERVLET_AUTOWIRE = "harmony.servlet.autowire";

    public static final String CONTEXT_PARAM_BUILDER = "harmony.servlet.config-builder";

    public static final String CONTEXT_PARAM_SHOW_INFO = "harmony.servlet.show-info";

    public static final String CONTEXT_PARAM_SCAN_HANDLES_TYPES = "harmony.servlet.scan-handlers-types";

    public static final String CONTEXT_PARAM_SCAN_INIT = "harmony.servlet.scan-init";

    public static final String CONTEXT_PARAM_SCAN_ASYNC = "harmony.servlet.scan-async";

    // store key

    public static final String CONTEXT_ATTRIBUTE_APP_CONFIG = ApplicationConfiguration.class.getName() + ".app-config";

    private WebXmlConstant() {
    }

}

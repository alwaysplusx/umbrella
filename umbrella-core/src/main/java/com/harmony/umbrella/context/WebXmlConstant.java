package com.harmony.umbrella.context;

/**
 * @author wuxii@foxmail.com
 */
public abstract class WebXmlConstant {

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

    public static final String APPLICATION_CFG_DATASOURCE = "harmony.cfg.datasource";

    public static final String APPLICATION_CFG_SCAN_PACKAGES = "harmony.cfg.scan-packages";

    public static final String APPLICATION_CFG_INITIALIZER = "harmony.cfg.initializer";

    public static final String APPLICATION_CFG_SHUTDOWN_HOOKS = "harmony.cfg.shutdown-hooks";

    public static final String APPLICATION_CFG_PROPERTIES = "harmony.cfg.properties";

    public static final String APPLICATION_CFG_PROPERTIES_SCAN_INIT = APPLICATION_CFG_PROPERTIES + ".scan-init";

    public static final String APPLICATION_CFG_PROPERTIES_SCAN_ASYNC = APPLICATION_CFG_PROPERTIES + ".scan-async";

    public static final String APPLICATION_CFG_PROPERTIES_SCAN_HANDLES_TYPES = APPLICATION_CFG_PROPERTIES + ".scan-handles-types";

    public static final String APPLICATION_CFG_PROPERTIES_SHOW_INFO = APPLICATION_CFG_PROPERTIES + ".show-info";

    public static final String APPLICATION_CFG_PROPERTIES_LISTENER_AUTOWIRE = APPLICATION_CFG_PROPERTIES + ".listener-autowire";

    public static final String APPLICATION_CFG_PROPERTIES_HOOK_AUTOWIRE = APPLICATION_CFG_PROPERTIES + ".hook-autowire";

    public static final String APPLICATION_CFG_PROPERTIES_FOCUS_SHUTDOWN = APPLICATION_CFG_PROPERTIES + ".focus-shutdown";

    public static final String APPLICATION_CFG_PROPERTIES_CUSTOM_BUILDER = APPLICATION_CFG_PROPERTIES + ".custom-builder";

    public static final String APPLICATION_CFG_SCAN_PACKAGES_VALUE = "com.harmony";

    public static final String APPLICATION_CFG_DATASOURCE_VALUE = "jdbc/harmony";

    public static final String CONTAINER_CONTEXT_ATTRIBUTE_APP_CONFIG = ApplicationConfiguration.class.getName() + ".app-config";

    private WebXmlConstant() {
    }

}

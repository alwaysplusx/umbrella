package com.harmony.umbrella.context;

import java.util.Map;

/**
 * 应用上下文的provider. 让应用可以在使用时候选择创建何种的应用环境
 * 
 * @author wuxii@foxmail.com
 */
public interface ApplicationContextProvider {

    ApplicationContext createApplicationContext(Map applicationProperties);

}

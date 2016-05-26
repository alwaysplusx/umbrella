package com.harmony.umbrella.context;

/**
 * 应用上下文的provider. 让应用可以在使用时候选择创建何种的应用环境
 * 
 * @author wuxii@foxmail.com
 */
public interface ContextProvider {

    ApplicationContext createApplicationContext();

}

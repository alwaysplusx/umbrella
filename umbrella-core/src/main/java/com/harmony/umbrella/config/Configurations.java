package com.harmony.umbrella.config;

import java.util.List;

/**
 * @author wuxii@foxmail.com
 */
public interface Configurations {

    /**
     * 应用所依赖的系统配置
     */
    String APPLICATION_CONFIGURATIONS = "ApplicationConfigurations";

    <T> T getBean(String beanName);

    <T> List<T> getBeans(String beanName);

}

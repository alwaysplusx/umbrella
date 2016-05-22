package com.harmony.umbrella.config;

import java.util.List;

/**
 * FIXME 在ejb环境下无法江transient修饰符字段传递出
 * 
 * @author wuxii@foxmail.com
 */
public interface Configurations {

    <T> T getBean(String beanName);

    <T> List<T> getBeans(String beanName);

}

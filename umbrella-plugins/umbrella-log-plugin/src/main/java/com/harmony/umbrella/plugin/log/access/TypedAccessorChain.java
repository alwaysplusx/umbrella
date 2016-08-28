package com.harmony.umbrella.plugin.log.access;

/**
 * 属性类型解析工具的持有者，通过支持判断来解析对应的目标
 * 
 * @author wuxii@foxmail.com
 */
public interface TypedAccessorChain {

    Object getValue(String name, Object obj);

    void setValue(String name, Object target, Object value);

}

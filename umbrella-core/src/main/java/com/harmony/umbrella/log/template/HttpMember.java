package com.harmony.umbrella.log.template;

/**
 * http 成员抽象类
 * 
 * @author wuxii@foxmail.com
 */
public abstract class HttpMember {

    /**
     * 通过名称获取成员变量
     * 
     * @param name
     *            成员名称
     * @return 成员值
     */
    public abstract Object get(String name);

}
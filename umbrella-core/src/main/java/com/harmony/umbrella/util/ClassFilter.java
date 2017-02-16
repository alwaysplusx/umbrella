package com.harmony.umbrella.util;

/**
 * 类过滤
 *
 * @author wuxii@foxmail.com
 */
public interface ClassFilter {

    /**
     * 过滤类的信息
     *
     * @param c
     *            待校验的class
     * @return true is accept
     */
    boolean accept(Class<?> c);

}
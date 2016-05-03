package com.harmony.umbrella.io;

/**
 * 资源过滤器
 * 
 * @author wuxii@foxmail.com
 */
public interface ResourceFilter {

    /**
     * 过滤资源，通过过滤条件返回true, 不通过返回false
     * 
     * @param resource
     *            待过滤的资源
     * @return 通过true, 不通过false
     */
    boolean accept(Resource resource);

}
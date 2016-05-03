package com.harmony.umbrella.cache.jmx;

import net.sf.ehcache.management.CacheManagerMBean;

/**
 * @author wuxii@foxmail.com
 */
public interface JmxCachePoolMBean extends CacheManagerMBean {

    String snapshot();

    boolean exists(String key);

    boolean clear();

    boolean clear(String key);

}

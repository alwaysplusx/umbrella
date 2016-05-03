//package com.harmony.umbrella.cache.jmx;
//
//import net.sf.ehcache.management.CacheManager;
//
///**
// * @author wuxii@foxmail.com
// */
//public class JmxCachePool extends CacheManager implements JmxCachePoolMBean {
//
//    private net.sf.ehcache.CacheManager cacheManager;
//
//    public JmxCachePool(net.sf.ehcache.CacheManager cacheManager) {
//        super(cacheManager);
//        this.cacheManager = cacheManager;
//    }
//
//    @Override
//    public boolean clear() {
//        return false;
//    }
//
//    @Override
//    public String snapshot() {
//        return null;
//    }
//
//    @Override
//    public boolean exists(String key) {
//        return false;
//    }
//
//    @Override
//    public boolean clear(String key) {
//        return false;
//    }
//
//}

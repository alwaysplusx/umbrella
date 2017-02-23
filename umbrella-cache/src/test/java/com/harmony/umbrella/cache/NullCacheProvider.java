package com.harmony.umbrella.cache;

import java.util.List;
import java.util.Properties;

/**
 * @author winterlau
 */
public class NullCacheProvider implements CacheProvider {

    private final static NullCache cache = new NullCache();

    @Override
    public String name() {
        return "none";
    }

    /* (non-Javadoc)
     * @see net.oschina.j2cache.CacheProvider#buildCache(java.lang.String, boolean, net.oschina.j2cache.CacheExpiredListener)
     */
    @Override
    public Cache build(String regionName, boolean autoCreate) throws CacheException {
        return cache;
    }

    /* (non-Javadoc)
     * @see net.oschina.j2cache.CacheProvider#start()
     */
    @Override
    public void start(Properties props) throws CacheException {
    }

    /* (non-Javadoc)
     * @see net.oschina.j2cache.CacheProvider#stop()
     */
    @Override
    public void stop() {
    }

    private static final class NullCache implements Cache {

        /* (non-Javadoc)
         * @see net.oschina.j2cache.Cache#get(java.lang.Object)
         */
        @Override
        public Object get(Object key) throws CacheException {
            return null;
        }

        /* (non-Javadoc)
         * @see net.oschina.j2cache.Cache#put(java.lang.Object, java.lang.Object)
         */
        @Override
        public void put(Object key, Object value) throws CacheException {
        }

        /* (non-Javadoc)
         * @see net.oschina.j2cache.Cache#update(java.lang.Object, java.lang.Object)
         */
        @Override
        public void update(Object key, Object value) throws CacheException {
        }

        /* (non-Javadoc)
         * @see net.oschina.j2cache.Cache#keys()
         */
        @Override
        @SuppressWarnings("rawtypes")
        public List keys() throws CacheException {
            return null;
        }

        /* (non-Javadoc)
         * @see net.oschina.j2cache.Cache#remove(java.lang.Object)
         */
        @Override
        public void evict(Object key) throws CacheException {
        }

        /* (non-Javadoc)
         * @see net.oschina.j2cache.Cache#batchRemove(java.util.List)
         */
        @Override
        @SuppressWarnings("rawtypes")
        public void evict(List keys) throws CacheException {
        }

        /* (non-Javadoc)
         * @see net.oschina.j2cache.Cache#clear()
         */
        @Override
        public void clear() throws CacheException {
        }

        /* (non-Javadoc)
         * @see net.oschina.j2cache.Cache#destroy()
         */
        @Override
        public void destroy() throws CacheException {
        }

        @Override
        public void put(Object key, Object value, Integer expireInSec) throws CacheException {
        }

        @Override
        public void update(Object key, Object value, Integer expireInSec) throws CacheException {
        }

    }
}

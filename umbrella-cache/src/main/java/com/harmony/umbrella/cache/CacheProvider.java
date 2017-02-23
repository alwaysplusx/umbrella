package com.harmony.umbrella.cache;

import java.util.Properties;

/**
 * Support for pluggable caches.
 * 
 * @author liudong
 */
public interface CacheProvider {

    /**
     * 缓存的标识名称
     * 
     * @return return cache provider name
     */
    public String name();

    /**
     * Configure the cache
     *
     * @param regionName
     *            the name of the cache region
     * @param autoCreate
     *            autoCreate settings
     * @return return cache instance
     * @throws CacheException
     *             cache exception
     */
    public Cache build(String regionName, boolean autoCreate) throws CacheException;

    /**
     * Callback to perform any necessary initialization of the underlying cache
     * implementation during SessionFactory construction.
     *
     * @param props
     *            current configuration settings.
     */
    public void start(Properties props) throws CacheException;

    /**
     * Callback to perform any necessary cleanup of the underlying cache
     * implementation during SessionFactory.close().
     */
    public void stop();

}

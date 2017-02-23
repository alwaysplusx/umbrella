package com.harmony.umbrella.cache;

import java.util.List;
import java.util.Map;

/**
 * Cache Channel
 * 
 * @author winterlau
 */
public interface CacheChannel {

    byte LEVEL_1 = 1;

    byte LEVEL_2 = 2;

    /**
     * 获取缓存中的数据
     * 
     * @param region:
     *            Cache Region name
     * @param key:
     *            Cache key
     * @return cache object
     */
    CacheObject get(String region, Object key);

    /**
     * 写入缓存
     * 
     * @param region:
     *            Cache Region name
     * @param key:
     *            Cache key
     * @param value:
     *            Cache value
     */
    void set(String region, Object key, Object value);

    /**
     * 写入缓存，并设置过期时间
     * 
     * @param region
     * @param key
     * @param value
     * @param expireInSec
     */
    void set(String region, Object key, Object value, Integer expireInSec);

    /**
     * 删除缓存
     * 
     * @param region:
     *            Cache Region name
     * @param key:
     *            Cache key
     */
    void evict(String region, Object key);

    /**
     * 批量删除缓存
     * 
     * @param region:
     *            Cache region name
     * @param keys:
     *            Cache key
     */
    void batchEvict(String region, List keys);

    /**
     * Clear the cache
     * 
     * @param region:
     *            Cache region name
     */
    void clear(String region) throws CacheException;

    /**
     * Get cache region keys
     * 
     * @param region:
     *            Cache region name
     * @return key list
     */
    List keys(String region) throws CacheException;

    /**
     * 关闭到通道的连接
     */
    void close();

    void start(Map properties);

    void stop();
}

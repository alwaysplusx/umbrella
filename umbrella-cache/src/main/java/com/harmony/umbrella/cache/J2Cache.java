package com.harmony.umbrella.cache;

import java.util.Map;
import java.util.Properties;

import com.harmony.umbrella.cache.channel.JGroupsCacheChannel;
import com.harmony.umbrella.cache.channel.RedisCacheChannel;

/**
 * 缓存入口
 * 
 * @author winterlau
 */
public class J2Cache {

    private static J2Cache INSTANCE;

    private final Object lock = new Object();

    private CacheChannel channel;

    private J2Cache() {
    }

    public static J2Cache getInstance() {
        if (INSTANCE == null) {
            synchronized (J2Cache.class) {
                if (INSTANCE == null) {
                    INSTANCE = new J2Cache();
                }
            }
        }
        return INSTANCE;
    }

    public CacheChannel getChannel() {
        synchronized (lock) {
            if (channel == null) {
                throw new IllegalStateException("cache channel not init!");
            }
        }
        return channel;
    }

    public void init(Properties properties) {
        if (channel != null) {
            throw new IllegalStateException("cache channel has aready been init!");
        }
        synchronized (lock) {
            if (channel != null) {
                throw new IllegalStateException("cache channel has aready been init!");
            }
            channel = buildCacheChannel(properties);
        }
    }

    public void destroy() {
        if (channel != null) {
            synchronized (lock) {
                if (channel != null) {
                    channel.close();
                    channel = null;
                }
            }
        }
    }

    private static CacheChannel buildCacheChannel(Map properties) {
        CacheChannel channel = null;
        String cache_broadcast = (String) properties.get("cache.broadcast");
        if ("redis".equalsIgnoreCase(cache_broadcast)) {
            channel = new RedisCacheChannel();
        } else if ("jgroups".equalsIgnoreCase(cache_broadcast)) {
            channel = new JGroupsCacheChannel();
        } else {
            throw new CacheException("Cache Channel not defined. name = " + cache_broadcast);
        }
        channel.start(properties);
        return channel;
    }

}

package com.harmony.umbrella.cache;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.harmony.umbrella.cache.ehcache.EhCacheProvider;
import com.harmony.umbrella.cache.redis.RedisCacheProvider;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

/**
 * 缓存管理器
 * 
 * @author Winter Lau
 */
public class CacheManager {

    private static final Log log = Logs.getLog(CacheManager.class);

    private static CacheManager INSTANCE;

    private CacheProvider l1_provider;
    private CacheProvider l2_provider;

    protected CacheExpiredListener listener;

    private String serializer;

    private CacheManager() {
    }

    public static CacheManager getInstance() {
        if (INSTANCE == null) {
            synchronized (J2Cache.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CacheManager();
                }
            }
        }
        return INSTANCE;
    }

    protected CacheProvider getCacheProvider(int level) {
        return level == 1 ? l1_provider : l2_provider;
    }

    protected Cache getCache(int level, String cacheName, boolean autoCreate) {
        return getCacheProvider(level).build(cacheName, autoCreate);
    }

    public void init(Map properties) {
        try {
            this.l1_provider = getProviderInstance((String) properties.get("cache.L1.provider_class"));
            this.l1_provider.start(getProviderProperties(properties, this.l1_provider));
            log.info("Using L1 CacheProvider : " + l1_provider.getClass().getName());

            this.l2_provider = getProviderInstance((String) properties.get("cache.L2.provider_class"));
            this.l2_provider.start(getProviderProperties(properties, this.l2_provider));
            log.info("Using L2 CacheProvider : " + l2_provider.getClass().getName());

            this.serializer = (String) properties.get("cache.serialization");
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    private final static CacheProvider getProviderInstance(String value) throws Exception {
        if ("ehcache".equalsIgnoreCase(value))
            return new EhCacheProvider();
        if ("redis".equalsIgnoreCase(value))
            return new RedisCacheProvider();
        if ("none".equalsIgnoreCase(value))
            return new NullCacheProvider();
        return (CacheProvider) Class.forName(value).newInstance();
    }

    private final static Properties getProviderProperties(Map props, CacheProvider provider) {
        Properties new_props = new Properties();
        Iterator keys = props.keySet().iterator();
        String prefix = provider.name() + '.';
        while (keys.hasNext()) {
            String key = String.valueOf(keys.next());
            if (key.startsWith(prefix))
                new_props.setProperty(key.substring(prefix.length()), String.valueOf(props.get(key)));
        }
        return new_props;
    }

    public void shutdown(int level) {
        getCacheProvider(level).stop();
    }

    public void shutdownAll() {
        l1_provider.stop();
        l2_provider.stop();
    }

    public void setCacheExpiredListener(CacheExpiredListener listener) {
        this.listener = listener;
    }

    public String getSerializer() {
        return serializer;
    }

    /**
     * 获取缓存中的数据
     * 
     * @param level
     *            Cache Level: L1 and L2
     * @param name
     *            Cache region name
     * @param key
     *            Cache key
     * @return Cache object
     */
    public Object get(int level, String name, Object key) {
        if (name != null && key != null) {
            Cache cache = getCache(level, name, false);
            if (cache != null) {
                return cache.get(key);
            }
        }
        return null;
    }

    /**
     * 获取缓存中的数据
     * 
     * @param level
     *            Cache Level -&gt; L1 and L2
     * @param resultClass
     *            Cache object class
     * @param name
     *            Cache region name
     * @param key
     *            Cache key
     * @return Cache object
     */
    public <T> T get(int level, Class<T> resultClass, String name, Object key) {
        if (name != null && key != null) {
            Cache cache = getCache(level, name, false);
            if (cache != null) {
                return (T) cache.get(key);
            }
        }
        return null;
    }

    /**
     * 写入缓存
     * 
     * @param level
     *            Cache Level: L1 and L2
     * @param name
     *            Cache region name
     * @param key
     *            Cache key
     * @param value
     *            Cache value
     */
    public void set(int level, String name, Object key, Object value) {
        if (name != null && key != null && value != null) {
            Cache cache = getCache(level, name, true);
            if (cache != null) {
                cache.put(key, value);
            }
        }
    }

    public void set(int level, String name, Object key, Object value, Integer expireInSec) {
        if (name != null && key != null && value != null) {
            Cache cache = getCache(level, name, true);
            if (cache != null) {
                cache.put(key, value, expireInSec);
            }
        }
    }

    /**
     * 清除缓存中的某个数据
     * 
     * @param level
     *            Cache Level: L1 and L2
     * @param name
     *            Cache region name
     * @param key
     *            Cache key
     */
    public void evict(int level, String name, Object key) {
        if (name != null && key != null) {
            Cache cache = getCache(level, name, false);
            if (cache != null) {
                cache.evict(key);
            }
        }
    }

    /**
     * 批量删除缓存中的一些数据
     * 
     * @param level
     *            Cache Level： L1 and L2
     * @param name
     *            Cache region name
     * @param keys
     *            Cache keys
     */
    public void batchEvict(int level, String name, List keys) {
        if (name != null && keys != null && keys.size() > 0) {
            Cache cache = getCache(level, name, false);
            if (cache != null)
                cache.evict(keys);
        }
    }

    /**
     * Clear the cache
     * 
     * @param level
     *            Cache level
     * @param name
     *            cache region name
     */
    public void clear(int level, String name) throws CacheException {
        Cache cache = getCache(level, name, false);
        if (cache != null) {
            cache.clear();
        }
    }

    /**
     * list cache keys
     * 
     * @param level
     *            Cache level
     * @param name
     *            cache region name
     * @return Key List
     */
    public List keys(int level, String name) throws CacheException {
        Cache cache = getCache(level, name, false);
        return (cache != null) ? cache.keys() : null;
    }

}

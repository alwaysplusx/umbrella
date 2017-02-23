package com.harmony.umbrella.cache;

import static com.harmony.umbrella.cache.AbstractCacheChannel.TriggerType.*;

import java.util.List;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractCacheChannel implements CacheChannel {

    protected abstract CacheManager getCacheManager();

    protected abstract MessagePublish getMessagePublish();

    @Override
    public CacheObject get(String region, Object key) {
        CacheManager cacheManager = getCacheManager();
        CacheObject o = new CacheObject(region, key);
        if (region != null && key != null) {
            o.setValue(cacheManager.get(LEVEL_1, region, key));
            if (o.getValue() == null) {
                o.setValue(cacheManager.get(LEVEL_2, region, key));
                if (o.getValue() != null) {
                    o.setLevel(LEVEL_2);
                    cacheManager.set(LEVEL_1, region, key, o.getValue());
                }
            } else
                o.setLevel(LEVEL_1);
        }
        return o;
    }

    @Override
    public void set(String region, Object key, Object value) {
        if (region != null && key != null) {
            if (value == null)
                evict(region, key);
            else {
                CacheManager cacheManager = getCacheManager();
                trigger(DELETE, region, key);
                cacheManager.set(LEVEL_1, region, key, value);
                cacheManager.set(LEVEL_2, region, key, value);
            }
        }
    }

    @Override
    public void set(String region, Object key, Object value, Integer expireInSec) {
        if (region != null && key != null) {
            if (value == null)
                evict(region, key);
            else {
                CacheManager cacheManager = getCacheManager();
                trigger(DELETE, region, key);
                cacheManager.set(LEVEL_1, region, key, value, expireInSec);
                cacheManager.set(LEVEL_2, region, key, value, expireInSec);
            }
        }
    }

    @Override
    public void evict(String region, Object key) {
        CacheManager cacheManager = getCacheManager();
        cacheManager.evict(LEVEL_1, region, key); //删除一级缓存
        cacheManager.evict(LEVEL_2, region, key); //删除二级缓存
        trigger(DELETE, region, key);
    }

    @Override
    public void batchEvict(String region, List keys) {
        CacheManager cacheManager = getCacheManager();
        cacheManager.batchEvict(LEVEL_1, region, keys);
        cacheManager.batchEvict(LEVEL_2, region, keys);
        trigger(DELETE, region, keys);
    }

    @Override
    public void clear(String region) throws CacheException {
        CacheManager cacheManager = getCacheManager();
        cacheManager.clear(LEVEL_1, region);
        cacheManager.clear(LEVEL_2, region);
        trigger(CLEAR, region, null);
    }

    @Override
    public List keys(String region) throws CacheException {
        return getCacheManager().keys(LEVEL_1, region);
    }

    @Override
    public void close() {
        getCacheManager().shutdownAll();
    }

    protected void trigger(TriggerType type, String region, Object key) {
        Command cmd = new Command(type.opt, region, key == null ? "" : key);
        try {
            getMessagePublish().publish(cmd);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to clear cache,region=" + region, e);
        }
    }

    protected void deleteCacheKey(String region, Object key) {
        CacheManager cacheManager = getCacheManager();
        if (key instanceof List) {
            cacheManager.batchEvict(LEVEL_1, region, (List) key);
        } else {
            cacheManager.evict(LEVEL_1, region, key);
        }
    }

    /**
     * 清除一级缓存的键对应内容
     * 
     * @param region
     *            Cache region name
     */
    protected void clearCacheKey(String region) {
        getCacheManager().clear(LEVEL_1, region);
    }

    protected static enum TriggerType {

        DELETE(Command.OPT_DELETE_KEY), //
        CLEAR(Command.OPT_CLEAR_KEY);

        byte opt;

        private TriggerType(byte opt) {
            this.opt = opt;
        }
    }

    public interface MessagePublish {

        void publish(Command cmd);

    }

    public interface MessageSubscriber {

        void onMessage(Command cmd);

    }
}

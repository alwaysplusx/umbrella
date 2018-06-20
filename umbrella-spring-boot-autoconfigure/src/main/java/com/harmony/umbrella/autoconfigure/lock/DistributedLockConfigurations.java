package com.harmony.umbrella.autoconfigure.lock;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.util.Assert;

/**
 * @author wuxii@foxmail.com
 */
final class DistributedLockConfigurations {

    private static final Map<LockType, Class<?>> MAPPINGS;

    static {
        Map<LockType, Class<?>> mappings = new EnumMap<>(LockType.class);
        // mappings.put(LockType.JDBC, value);
        mappings.put(LockType.REDIS, RedisLockAutoConfiguration.class);
        mappings.put(LockType.ZOOKEEPER, ZookeeperLockAutoConfiguration.class);
        MAPPINGS = Collections.unmodifiableMap(mappings);
    }

    private DistributedLockConfigurations() {
    }

    public static String getConfigurationClass(CacheType cacheType) {
        Class<?> configurationClass = MAPPINGS.get(cacheType);
        Assert.state(configurationClass != null, () -> "Unknown cache type " + cacheType);
        return configurationClass.getName();
    }

    public static LockType getType(String configurationClassName) {
        for (Map.Entry<LockType, Class<?>> entry : MAPPINGS.entrySet()) {
            if (entry.getValue().getName().equals(configurationClassName)) {
                return entry.getKey();
            }
        }
        throw new IllegalStateException("Unknown configuration class " + configurationClassName);
    }
}

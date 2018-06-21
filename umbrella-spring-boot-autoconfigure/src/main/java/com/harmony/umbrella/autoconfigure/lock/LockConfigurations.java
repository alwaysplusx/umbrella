package com.harmony.umbrella.autoconfigure.lock;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import org.springframework.util.Assert;

/**
 * @author wuxii@foxmail.com
 */
final class LockConfigurations {

    private static final Map<LockType, Class<?>> MAPPINGS;

    static {
        Map<LockType, Class<?>> mappings = new EnumMap<>(LockType.class);
        mappings.put(LockType.JDBC, JdbcLockAutoConfiguration.class);
        mappings.put(LockType.REDIS, RedisLockAutoConfiguration.class);
        mappings.put(LockType.ZOOKEEPER, ZookeeperLockAutoConfiguration.class);
        MAPPINGS = Collections.unmodifiableMap(mappings);
    }

    private LockConfigurations() {
    }

    public static String getConfigurationClass(LockType lockType) {
        Class<?> configurationClass = MAPPINGS.get(lockType);
        Assert.state(configurationClass != null, () -> "Unknown cache type " + lockType);
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

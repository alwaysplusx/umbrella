package com.harmony.umbrella.lock;

import org.springframework.integration.support.locks.LockRegistry;

import java.util.concurrent.locks.Lock;

/**
 * @author wuxii
 */
public interface ConfigurableLockRegistry extends LockRegistry {

    int DEFAULT_TIMEOUT = 6000;

    @Override
    default Lock obtain(Object lockKey) {
        return obtain(lockKey, DEFAULT_TIMEOUT);
    }

    Lock obtain(Object lockKey, int timeout);

}

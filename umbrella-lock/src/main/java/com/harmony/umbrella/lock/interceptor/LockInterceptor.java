package com.harmony.umbrella.lock.interceptor;

import com.harmony.umbrella.lock.ConfigurableLockRegistry;
import com.harmony.umbrella.lock.annotation.Lock;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * @author wuxii
 */
public class LockInterceptor implements MethodInterceptor {

    private ConfigurableLockRegistry lockRegistry;

    public LockInterceptor() {
    }

    public LockInterceptor(ConfigurableLockRegistry lockRegistry) {
        this.lockRegistry = lockRegistry;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Lock lockAttribute = getLockAttribute(invocation.getMethod());
        int timeout = lockAttribute.timeout();
        Object lockKey = lockIdentificationKey(lockAttribute);
        java.util.concurrent.locks.Lock lock = lockRegistry.obtain(lockKey, timeout);
        try {
            lock.lock();
            return invocation.proceed();
        } finally {
            lock.unlock();
        }
    }

    private Object invokeWithinLock(Method method, Supplier proceed) throws Throwable {
        return null;
    }

    protected Object lockIdentificationKey(Lock lock) {
        return null;
    }

    private Lock getLockAttribute(Method method) {
        // TODO 使用注解来获取锁
        return null;
    }

    public void setLockRegistry(ConfigurableLockRegistry lockRegistry) {
        this.lockRegistry = lockRegistry;
    }

}

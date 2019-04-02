package com.harmony.umbrella.lock.interceptor;

import java.lang.reflect.Method;

/**
 * @author wuxii
 */
public interface KeyGenerator {

	Object generate(Object target, Method method, Object... params);

}

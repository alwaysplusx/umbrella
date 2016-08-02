package com.harmony.umbrella.ee.support;

/**
 * @author wuxii@foxmail.com
 */
public interface LocalInterfaceTransformer {

    @SuppressWarnings("rawtypes")
    Class[] transform(Class<?> localInterface);

}

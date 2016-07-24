package com.harmony.umbrella.context.ee.support;

import java.util.Collection;
import java.util.Set;

/**
 * @author wuxii@foxmail.com
 */
public interface JndiFormatter {

    @SuppressWarnings("rawtypes")
    Set<String> format(Collection<String> beanNames, Collection<Class> beanInterfaces);

}

package com.harmony.umbrella.ee.formatter;

import java.util.Collection;

/**
 * @author wuxii@foxmail.com
 */
public interface JndiFormatter {

    @SuppressWarnings("rawtypes")
    Collection<String> format(Collection<String> beanNames, Collection<Class> beanInterfaces);

}

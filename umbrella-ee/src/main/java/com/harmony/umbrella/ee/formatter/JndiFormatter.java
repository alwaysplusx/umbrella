package com.harmony.umbrella.ee.formatter;

import java.util.Collection;

/**
 * @author wuxii@foxmail.com
 */
public interface JndiFormatter {

    Collection<String> format(Collection<String> beanNames, Collection<Class> beanInterfaces);

}

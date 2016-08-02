package com.harmony.umbrella.ee.support;

import java.util.Set;

import com.harmony.umbrella.ee.BeanDefinition;

/**
 * @author wuxii@foxmail.com
 */
public interface PartResolver<T> {

    Set<T> resolve(BeanDefinition bd);

}

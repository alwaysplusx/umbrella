package com.harmony.umbrella.context.ee.support;

import java.util.Set;

import com.harmony.umbrella.context.ee.BeanDefinition;

/**
 * @author wuxii@foxmail.com
 */
public interface PartResolver<T> {

    Set<T> resolve(BeanDefinition bd);

}

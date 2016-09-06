package com.harmony.umbrella.data.support;

import com.harmony.umbrella.data.Specification;

/**
 * @author wuxii@foxmail.com
 */
public interface LogicalSpecification<T> extends Specification<T> {

    boolean isAnd();

    boolean isOr();

}

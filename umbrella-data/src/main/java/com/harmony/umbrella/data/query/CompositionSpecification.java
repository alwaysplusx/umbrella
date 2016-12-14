package com.harmony.umbrella.data.query;

import com.harmony.umbrella.data.CompositionType;
import com.harmony.umbrella.data.Specification;

/**
 * @author wuxii@foxmail.com
 */
public interface CompositionSpecification<T> extends Specification<T> {

    CompositionType getCompositionType();

}

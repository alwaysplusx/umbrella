package com.harmony.umbrella.data.query;

import org.springframework.data.jpa.domain.Specification;

import com.harmony.umbrella.data.CompositionType;

/**
 * @author wuxii@foxmail.com
 */
public interface CompositionSpecification<T> extends Specification<T> {

    CompositionType getCompositionType();

}

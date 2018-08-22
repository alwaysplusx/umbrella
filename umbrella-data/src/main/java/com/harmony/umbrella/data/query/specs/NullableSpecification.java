package com.harmony.umbrella.data.query.specs;

import org.springframework.data.jpa.domain.Specification;

/**
 * @author wuxii@foxmail.com
 */
public interface NullableSpecification<T> extends Specification<T> {

    static <T> NullableSpecification<T> of(Specification<T> spec) {
        return (root, query, builder) -> spec.toPredicate(root, query, builder);
    }

}

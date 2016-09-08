package com.harmony.umbrella.data.util;

import java.util.List;

import com.harmony.umbrella.data.Specification;
import com.harmony.umbrella.data.domain.Pageable;
import com.harmony.umbrella.data.domain.Sort;

/**
 * @author wuxii@foxmail.com
 */
public class QueryBundle<M> {

    Class<M> entityClass;

    Pageable pageable;

    Specification specification;

    List<String> fetchAttributes;

    boolean distinct;

    public Class<M> getEntityClass() {
        return entityClass;
    }

    public Pageable getPageable() {
        return pageable;
    }

    public Specification getSpecification() {
        return specification;
    }

    public List<String> getFetchAttributes() {
        return fetchAttributes;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public Sort getSort() {
        return pageable == null ? null : pageable.getSort();
    }

}

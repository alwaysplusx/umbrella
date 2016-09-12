package com.harmony.umbrella.data.util;

import com.harmony.umbrella.data.Specification;
import com.harmony.umbrella.data.domain.Pageable;
import com.harmony.umbrella.data.domain.Sort;
import com.harmony.umbrella.data.util.QueryBuilder.FetchAttributes;
import com.harmony.umbrella.data.util.QueryBuilder.JoinAttributes;

/**
 * @author wuxii@foxmail.com
 */
public interface QueryBundle<M> {

    Class<M> getEntityClass();

    Pageable getPageable();

    default int getPageNumber() {
        Pageable pageable = getPageable();
        return pageable != null ? pageable.getPageNumber() : 0;
    }

    default int getPageSize() {
        Pageable pageable = getPageable();
        return pageable != null ? pageable.getPageSize() : 20;
    }

    default Sort getSort() {
        Pageable pageable = getPageable();
        return pageable != null ? pageable.getSort() : null;
    }

    Specification getSpecification();

    boolean isDistinct();

    FetchAttributes getFetchAttributes();

    JoinAttributes getJoinAttributes();
}

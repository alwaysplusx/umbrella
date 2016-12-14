package com.harmony.umbrella.data.query;

import com.harmony.umbrella.data.Specification;
import com.harmony.umbrella.data.domain.Pageable;
import com.harmony.umbrella.data.domain.Sort;
import com.harmony.umbrella.data.query.QueryBuilder.FetchAttributes;
import com.harmony.umbrella.data.query.QueryBuilder.JoinAttributes;

/**
 * @author wuxii@foxmail.com
 */
public interface QueryBundle<M> {

    Class<M> getEntityClass();

    Pageable getPageable();

    int getPageNumber();

    int getPageSize();

    Sort getSort();

    Specification getSpecification();

    int getQueryFeature();

    FetchAttributes getFetchAttributes();

    JoinAttributes getJoinAttributes();

}

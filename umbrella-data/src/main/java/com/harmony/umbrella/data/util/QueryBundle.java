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

    int getPageNumber();

    int getPageSize();

    Sort getSort();

    Specification getSpecification();

    boolean isDistinct();

    boolean isAllowEmptyCondition();
    
    FetchAttributes getFetchAttributes();

    JoinAttributes getJoinAttributes();
    
}

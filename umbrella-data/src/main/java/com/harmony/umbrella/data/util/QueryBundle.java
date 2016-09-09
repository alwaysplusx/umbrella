package com.harmony.umbrella.data.util;

import com.harmony.umbrella.data.Specification;
import com.harmony.umbrella.data.domain.Pageable;
import com.harmony.umbrella.data.domain.Sort;
import com.harmony.umbrella.data.util.QueryBuilder.FetchAttribute;
import com.harmony.umbrella.data.util.QueryBuilder.JoinAttribute;

/**
 * @author wuxii@foxmail.com
 */
public class QueryBundle<M> {

    Class<M> entityClass;

    Pageable pageable;

    Specification specification;

    FetchAttribute fetchAttribute;

    JoinAttribute joinAttribute;

    boolean distinct;

    public Class<M> getEntityClass() {
        return entityClass;
    }

    public int getPageNumber() {
        return pageable == null ? 0 : this.pageable.getPageNumber();
    }

    public int getPageSize() {
        return pageable == null ? 20 : this.pageable.getPageSize();
    }

    public Pageable getPageable() {
        return pageable;
    }

    public Specification getSpecification() {
        return specification;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public Sort getSort() {
        return pageable == null ? null : pageable.getSort();
    }

    public FetchAttribute getFetchAttribute() {
        return fetchAttribute;
    }

    public JoinAttribute getJoinAttribute() {
        return joinAttribute;
    }

}

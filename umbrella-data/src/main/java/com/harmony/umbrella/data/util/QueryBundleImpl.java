package com.harmony.umbrella.data.util;

import java.io.Serializable;

import com.harmony.umbrella.data.Specification;
import com.harmony.umbrella.data.domain.Pageable;
import com.harmony.umbrella.data.domain.Sort;
import com.harmony.umbrella.data.util.QueryBuilder.FetchAttributes;
import com.harmony.umbrella.data.util.QueryBuilder.JoinAttributes;

/**
 * @author wuxii@foxmail.com
 */
public class QueryBundleImpl<M> implements QueryBundle<M>, Serializable {

    private static final long serialVersionUID = 1L;

    Class<M> entityClass;

    Pageable pageable;

    Specification specification;

    FetchAttributes fetchAttributes;

    JoinAttributes joinAttributes;

    boolean distinct;

    boolean allowEmptyCondition;

    public QueryBundleImpl() {
    }

    public QueryBundleImpl(QueryBundle bundle) {
        this.entityClass = bundle.getEntityClass();
        this.pageable = bundle.getPageable();
        this.specification = bundle.getSpecification();
        this.fetchAttributes = bundle.getFetchAttributes();
        this.joinAttributes = bundle.getJoinAttributes();
        this.distinct = bundle.isDistinct();
    }

    @Override
    public Class<M> getEntityClass() {
        return entityClass;
    }

    public int getPageNumber() {
        return pageable == null ? 0 : this.pageable.getPageNumber();
    }

    public int getPageSize() {
        return pageable == null ? 20 : this.pageable.getPageSize();
    }

    @Override
    public Pageable getPageable() {
        return pageable;
    }

    @Override
    public Specification getSpecification() {
        return specification;
    }

    @Override
    public boolean isDistinct() {
        return distinct;
    }

    @Override
    public boolean isAllowEmptyCondition() {
        return allowEmptyCondition;
    }

    public Sort getSort() {
        return pageable == null ? null : pageable.getSort();
    }

    @Override
    public FetchAttributes getFetchAttributes() {
        return fetchAttributes;
    }

    @Override
    public JoinAttributes getJoinAttributes() {
        return joinAttributes;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("select * from ").append(entityClass != null ? entityClass.getSimpleName() : "UnknowType");
        if (specification != null) {
            out.append(" where ").append(specification);
        }
        return out.toString();
    }

}

package com.harmony.umbrella.data.query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.harmony.umbrella.data.query.QueryBuilder.FetchAttributes;
import com.harmony.umbrella.data.query.QueryBuilder.JoinAttributes;

/**
 * @author wuxii@foxmail.com
 */
public interface QueryBundle<M> {

    Class<M> getEntityClass();

    Pageable getPageable();

    Specification getSpecification();

    int getQueryFeature();

    FetchAttributes getFetchAttributes();

    JoinAttributes getJoinAttributes();

}

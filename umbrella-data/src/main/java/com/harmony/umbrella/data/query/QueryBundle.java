package com.harmony.umbrella.data.query;

import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.harmony.umbrella.data.query.QueryBuilder.FetchAttributes;
import com.harmony.umbrella.data.query.QueryBuilder.JoinAttributes;

/**
 * @author wuxii@foxmail.com
 */
public interface QueryBundle<M> {

    Class<M> getEntityClass();

    Specification getSpecification();

    int getQueryFeature();

    FetchAttributes getFetchAttributes();

    JoinAttributes getJoinAttributes();

    Set<String> getGrouping();

    int getPageNumber();

    int getPageSize();

    Sort getSort();

}

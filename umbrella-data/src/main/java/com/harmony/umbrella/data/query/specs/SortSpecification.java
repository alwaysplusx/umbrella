package com.harmony.umbrella.data.query.specs;

import java.io.Serializable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;

/**
 * @author wuxii@foxmail.com
 */
public class SortSpecification<T> implements NullableSpecification<T>, Serializable {

    private static final long serialVersionUID = 430336683524432193L;
    private final Sort sort;

    public SortSpecification(Sort sort) {
        this.sort = sort;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        query.orderBy(QueryUtils.toOrders(sort, root, cb));
        return null;
    }

}

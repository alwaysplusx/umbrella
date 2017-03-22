package com.harmony.umbrella.data.query.specs;

import java.io.Serializable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

/**
 * @author wuxii@foxmail.com
 */
public class HavingSpecification<T> implements Specification<T>, Serializable {

    private static final long serialVersionUID = 650763363436627664L;

    private Specification<T> spec;

    public HavingSpecification(Specification<T> spec) {
        this.spec = spec;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        query.having(spec.toPredicate(root, query, cb));
        return null;
    }

}

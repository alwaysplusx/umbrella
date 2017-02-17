package com.harmony.umbrella.data.query.specs;

import java.io.Serializable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;

import com.harmony.umbrella.data.Operator;

/**
 * @author wuxii@foxmail.com
 */
public class SubquerySpecification<T> implements Specification<T>, Serializable {

    private static final long serialVersionUID = 7843423831076612160L;
    private Class<T> entityClass;
    private Specification<T> condition;
    private String column;
    private Operator operator;
    private String selection;

    public SubquerySpecification(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Subquery<T> subquery = query.subquery(entityClass);
        Root<T> subRoot = subquery.from(entityClass);
        // XXX subquery not assignable from criteriaQuery, just pass null
        subquery.where(condition.toPredicate(subRoot, null, cb));
        subquery.select(subRoot.get(selection));
        Expression x = root.get(column);
        return operator.explain(x, cb, subquery);
    }

}

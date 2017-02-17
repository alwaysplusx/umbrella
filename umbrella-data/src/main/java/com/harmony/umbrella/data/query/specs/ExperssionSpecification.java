package com.harmony.umbrella.data.query.specs;

import java.io.Serializable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.harmony.umbrella.data.Operator;
import com.harmony.umbrella.data.query.QueryUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ExperssionSpecification<T> implements Specification<T>, Serializable {

    private static final long serialVersionUID = 1171738576029238894L;

    private String left;
    private String right;
    private Operator operator;

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Expression<Object> x = QueryUtils.toExpressionRecursively(root, left);
        Expression<Object> y = QueryUtils.toExpressionRecursively(root, right);
        return operator.explain(x, cb, y);
    }

}

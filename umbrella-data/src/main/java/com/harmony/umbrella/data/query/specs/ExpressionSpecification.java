package com.harmony.umbrella.data.query.specs;

import java.io.Serializable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.harmony.umbrella.data.Operator;
import com.harmony.umbrella.data.util.QueryUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ExpressionSpecification<T> implements Specification<T>, Serializable {

    private static final long serialVersionUID = 1171738576029238894L;

    private String x;
    private String y;
    private Operator operator;

    public ExpressionSpecification(String x, String y, Operator operator) {
        this.x = x;
        this.y = y;
        this.operator = operator;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return operator.explain(toExpression(x, root, cb), cb, toExpression(y, root, cb));
    }

    @Override
    public String toString() {
        return x + " " + operator.symbol() + " " + y;
    }

    protected Expression toExpression(String name, Root root, CriteriaBuilder cb) {
        return QueryUtils.isFunctionExpression(name) ? QueryUtils.functionExpression(name, root, cb, null) : QueryUtils.toExpressionRecursively(root, name);
    }
}

package com.harmony.umbrella.data.query.specs;

import java.io.Serializable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.harmony.umbrella.data.ExpressionExplainer;
import com.harmony.umbrella.data.util.QueryUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ConditionSpecification<T> implements Specification<T>, Serializable {

    private static final long serialVersionUID = 5012760558487273137L;

    private String x;
    private Object y;
    private ExpressionExplainer operator;
    private boolean autoJoin;

    public ConditionSpecification(String x, Object y, ExpressionExplainer operator) {
        this(x, y, operator, true);
    }

    public ConditionSpecification(String x, Object y, ExpressionExplainer operator, boolean autoJoin) {
        this.x = x;
        this.y = y;
        this.operator = operator;
        this.autoJoin = autoJoin;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Expression<?> expression = QueryUtils.parseExpression(x, root, cb, autoJoin);
        return operator.explain(expression, cb, y);
    }

    @Override
    public String toString() {
        return x + " " + operator + " " + "?";
    }

}

package com.harmony.umbrella.data;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;

/**
 * @author wuxii@foxmail.com
 */
public class QueryBuilder<T extends QueryBuilder<T>> {

    protected transient EntityManager entityManager;
    protected transient CriteriaBuilder builder;

    protected boolean autoStart = true;
    protected boolean autoFinish = true;

    protected boolean allowEmptyCondition;

    protected Class entityClass;

    // query condition method

    public T equal(String name, Object value) {
        return addCondition(name, value, Operator.EQUAL);
    }

    public T notEqual(String name, Object value) {
        return addCondition(name, value, Operator.NOT_EQUAL);
    }

    public T like(String name, Object value) {
        return addCondition(name, value, Operator.LIKE);
    }

    public T notLike(String name, Object value) {
        return addCondition(name, value, Operator.NOT_LIKE);
    }

    public T in(String name, Object value) {
        return addCondition(name, value, Operator.IN);
    }

    public T in(String name, Collection<?> value) {
        return addCondition(name, value, Operator.IN);
    }

    public T in(String name, Object... value) {
        return addCondition(name, value, Operator.IN);
    }

    public T notIn(String name, Object value) {
        return addCondition(name, value, Operator.NOT_IN);
    }

    public T notIn(String name, Collection<?> value) {
        return addCondition(name, value, Operator.NOT_IN);
    }

    public T notIn(String name, Object... value) {
        return addCondition(name, value, Operator.NOT_IN);
    }

    public T between(String name, Object left, Object right) {
        addCondition(name, left, Operator.GREATER_THAN_OR_EQUAL);
        addCondition(name, right, Operator.LESS_THAN_OR_EQUAL);
        return (T) this;
    }

    public T notBetween(String name, Object left, Object right) {
        addCondition(name, left, Operator.LESS_THAN);
        addCondition(name, right, Operator.GREATER_THAN);
        return (T) this;
    }

    public T greatThen(String name, Object value) {
        return addCondition(name, value, Operator.GREATER_THAN);
    }

    public T greatEqual(String name, Object value) {
        return addCondition(name, value, Operator.GREATER_THAN_OR_EQUAL);
    }

    public T lessThen(String name, Object value) {
        return addCondition(name, value, Operator.LESS_THAN);
    }

    public T lessEqual(String name, Object value) {
        return addCondition(name, value, Operator.LESS_THAN_OR_EQUAL);
    }

    public T isNull(String name) {
        return addCondition(name, null, Operator.NULL);
    }

    public T isNotNull(String name) {
        return addCondition(name, null, Operator.NOT_NULL);
    }

    // or condition

    public T addCondition(String name, Object value, Operator operator) {

        return (T) this;
    }

    protected Specification currentBind() {
        return null;
    }

    public T start() {
        return (T) this;
    }

    public T end() {
        return (T) this;
    }

    public T and(Specification spec) {
        return (T) this;
    }

    public T or(Specification spec) {
        return (T) this;
    }

}

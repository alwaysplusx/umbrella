package com.harmony.umbrella.data.query.select;

import com.harmony.umbrella.data.query.Column;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;

/**
 * @author wuxii
 */
public class CountBuilder<X> extends SelectionBuilder<X, CountBuilder<X>> {

    private String name;
    private boolean distinct;

    public CountBuilder() {
    }

    public CountBuilder(String name) {
        this.name = name;
    }

    @Override
    protected Column build(QueryModel model) {
        Expression expression = name == null ? model.getRoot() : model.get(name);
        CriteriaBuilder cb = model.getCriteriaBuilder();
        Expression<Long> countExpression = distinct ? cb.countDistinct(expression) : cb.count(expression);
        return new Column(name, alias, countExpression);
    }

    public CountBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public CountBuilder setDistinct(boolean distinct) {
        this.distinct = distinct;
        return this;
    }
}

package com.harmony.umbrella.data.query.select;

import com.harmony.umbrella.data.query.Column;

import javax.persistence.criteria.Expression;

/**
 * @author wuxii
 */
public class ColumnBuilder<X> extends SelectionBuilder<X, ColumnBuilder<X>> {

    private String name;

    public ColumnBuilder() {
    }

    public ColumnBuilder(String name) {
        this.name = name;
    }

    @Override
    protected Column build(QueryModel model) {
        Expression expression = model.get(name);
        return new Column(name, alias, expression);
    }

    public ColumnBuilder setName(String name) {
        this.name = name;
        return this;
    }

}

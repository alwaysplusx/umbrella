package com.harmony.umbrella.data.query.select;

import com.harmony.umbrella.data.model.ExpressionModel;
import com.harmony.umbrella.data.model.RootModel;
import com.harmony.umbrella.data.query.Column;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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
    public Column generate(Root<X> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        ExpressionModel expression = RootModel.of(root, cb).get(name);
        return new Column(name, alias, expression.toExpression());
    }

    public ColumnBuilder setName(String name) {
        this.name = name;
        return this;
    }


}

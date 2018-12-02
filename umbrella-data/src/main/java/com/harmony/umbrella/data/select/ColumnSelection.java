package com.harmony.umbrella.data.select;

import com.harmony.umbrella.data.SelectionBuilder;
import com.harmony.umbrella.data.Selections;
import com.harmony.umbrella.data.model.QueryModel;

import javax.persistence.criteria.Expression;

/**
 * @author wuxii
 */
public class ColumnSelection extends SelectionBuilder<ColumnSelection> {

    private String name;

    public ColumnSelection(Selections selections) {
        super(selections);
    }

    public ColumnSelection setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    protected Expression buildSelection(QueryModel queryModel) {
        Expression exp = queryModel.get(name);
        if (this.alias == null) {
            this.alias = name;
        }
        return exp;
    }

}

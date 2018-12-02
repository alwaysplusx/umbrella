package com.harmony.umbrella.data.select;

import com.harmony.umbrella.data.SelectionBuilder;
import com.harmony.umbrella.data.Selections;
import com.harmony.umbrella.data.model.QueryModel;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;

/**
 * @author wuxii
 */
public class CountSelection extends SelectionBuilder<CountSelection> {

    private String name;
    private boolean distinct;

    public CountSelection(Selections selections) {
        super(selections);
    }

    @Override
    protected Expression buildSelection(QueryModel queryModel) {
        Expression exp = queryModel.get(name);
        CriteriaBuilder cb = queryModel.getCriteriaBuilder();
        return distinct ? cb.countDistinct(exp) : cb.count(exp);
    }

    public CountSelection setName(String name) {
        this.name = name;
        return this;
    }

    public CountSelection setDistinct(boolean distinct) {
        this.distinct = distinct;
        return this;
    }
}

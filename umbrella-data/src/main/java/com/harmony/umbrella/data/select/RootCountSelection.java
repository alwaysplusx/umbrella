package com.harmony.umbrella.data.select;

import com.harmony.umbrella.data.SelectionBuilder;
import com.harmony.umbrella.data.Selections;
import com.harmony.umbrella.data.model.QueryModel;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

/**
 * @author wuxii
 */
public class RootCountSelection extends SelectionBuilder<RootCountSelection> {

    private boolean distinct;

    public RootCountSelection(Selections selections) {
        super(selections);
    }

    @Override
    protected Expression buildSelection(QueryModel queryModel) {
        CriteriaBuilder cb = queryModel.getCriteriaBuilder();
        Root root = queryModel.getRoot();
        return distinct ? cb.countDistinct(root) : cb.count(root);
    }

    public RootCountSelection setDistinct(boolean distinct) {
        this.distinct = distinct;
        return this;
    }

}

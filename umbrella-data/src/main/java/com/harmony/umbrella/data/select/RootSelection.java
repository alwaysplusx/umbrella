package com.harmony.umbrella.data.select;

import com.harmony.umbrella.data.SelectionBuilder;
import com.harmony.umbrella.data.Selections;
import com.harmony.umbrella.data.model.QueryModel;

import javax.persistence.criteria.Expression;

/**
 * @author wuxii
 */
public class RootSelection extends SelectionBuilder<RootSelection> {

    public RootSelection(Selections selections) {
        super(selections);
    }

    @Override
    protected Expression buildSelection(QueryModel queryModel) {
        return queryModel.getRoot();
    }

}

package com.harmony.umbrella.data.query.select;

import com.harmony.umbrella.data.query.Column;

/**
 * @author wuxii
 */
public class RootBuilder<X> extends SelectionBuilder<X, RootBuilder<X>> {

    @Override
    protected Column build(QueryModel model) {
        return new Column(null, null, model.getRoot());
    }

}

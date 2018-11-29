package com.harmony.umbrella.data.query.select;

import com.harmony.umbrella.data.query.Column;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * @author wuxii
 */
public class RootBuilder<X> extends SelectionBuilder<X, RootBuilder<X>> {

    @Override
    public Column generate(Root<X> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return new Column(null, null, root);
    }

}

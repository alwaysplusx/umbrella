package com.harmony.umbrella.data.query.specs;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.harmony.umbrella.data.query.Selections;

/**
 * @author wuxii@foxmail.com
 */
public class SelectionSpecification<T> implements NullableSpecification<T> {

    private static final long serialVersionUID = -8372941951177254159L;
    private final Selections<T> selections;

    public SelectionSpecification(Selections<T> selections) {
        this.selections = selections;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Expression<?>> selectionList = selections.select(root, query, cb);
        if (selectionList.size() == 1) {
            query.select((Expression) selectionList.get(0));
        } else {
            query.multiselect((List) selectionList);
        }
        return null;
    }
}

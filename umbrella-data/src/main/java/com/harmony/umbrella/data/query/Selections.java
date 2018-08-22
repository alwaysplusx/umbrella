package com.harmony.umbrella.data.query;

import com.harmony.umbrella.data.model.RootModel;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wuxii
 */
public interface Selections<T> {

    List<Selection> select(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb);

    static <T> Selections<T> root() {
        return (root, query, cb) -> new ArrayList<>(Arrays.asList(root));
    }

    static <T> Selections<T> count() {
        return (r, q, cb) -> Arrays.asList(cb.count(r));
    }

    static <T> Selections<T> count(String name) {
        return (r, q, cb) -> Arrays.asList(cb.count(RootModel.of(r, cb).get(name).toExpression()));
    }

    static <T> Selections<T> countDistinct() {
        return (r, q, cb) -> Arrays.asList(cb.countDistinct(r));
    }

    static <T> Selections<T> countDistinct(String name) {
        return (r, q, cb) -> Arrays.asList(cb.countDistinct(RootModel.of(r, cb).get(name).toExpression()));
    }

}

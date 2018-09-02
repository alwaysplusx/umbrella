package com.harmony.umbrella.data.model;

import com.harmony.umbrella.data.query.Selections;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wuxii
 */
public interface SelectionModel<T> extends Selections<T> {

    List<ExpressionModel> select(RootModel<T> rootModel, CriteriaQuery<?> query, CriteriaBuilder cb);

    @Override
    default List<Selection> select(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return new ArrayList<>(select(RootModel.of(root, cb), query, cb));
    }

    static <T> SelectionModel<T> of(String... names) {
        return (rootModel, query, cb) -> Stream.of(names).map(rootModel::get).collect(Collectors.toList());
    }

}

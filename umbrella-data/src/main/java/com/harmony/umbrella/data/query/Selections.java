package com.harmony.umbrella.data.query;

import com.harmony.umbrella.data.util.QueryUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wuxii
 */
public interface Selections<T> {

    List<Expression<?>> select(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb);

    static <T> Selections<T> root() {
        return new RootSelections<>();
    }

    static <T> Selections<T> of(String... columns) {
        return new ColumnSelections<>(columns);
    }

    static <T> Selections<T> function(String function, String column) {
        return new FunctionSelections<>(function, column);
    }

    static <T> Selections<T> count(String column, boolean distinct) {
        return new CountSelections<>(column, distinct);
    }

    static <T> Selections<T> count(boolean distinct) {
        return count(null, distinct);
    }

    class RootSelections<T> implements Selections<T> {
        @Override
        public List<Expression<?>> select(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            ArrayList<Expression<?>> result = new ArrayList<>();
            result.add(root);
            return result;
        }
    }

    class CountSelections<T> implements Selections<T> {

        private final String column;
        private final boolean distinct;

        CountSelections(String column, boolean distinct) {
            this.column = column;
            this.distinct = distinct;
        }

        @Override
        public List<Expression<?>> select(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            Expression exp = column == null ? root : QueryUtils.toExpressionRecursively(root, column);
            return new ArrayList<>(Arrays.asList(distinct ? cb.countDistinct(exp) : cb.count(exp)));
        }

    }

    class FunctionSelections<T> implements Selections<T> {

        private final String function;
        private final String column;

        FunctionSelections(String function, String column) {
            this.function = function;
            this.column = column;
        }

        @Override
        public List<Expression<?>> select(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            List<Expression<?>> result = new ArrayList<>();
            result.add(cb.function(function, null, QueryUtils.toExpressionRecursively(root, column)));
            return result;
        }

    }

    final class ColumnSelections<T> implements Selections<T> {

        private final List<String> columns;

        public ColumnSelections(String... columns) {
            this.columns = Arrays.asList(columns);
        }

        @Override
        public List<Expression<?>> select(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            List<Expression<?>> cs = new ArrayList<>();
            for (String c : columns) {
                cs.add(QueryUtils.parseExpression(c, root, cb));
            }
            return cs;
        }

    }
}

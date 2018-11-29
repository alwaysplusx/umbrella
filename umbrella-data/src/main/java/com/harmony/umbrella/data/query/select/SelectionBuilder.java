package com.harmony.umbrella.data.query.select;

import com.harmony.umbrella.data.query.Column;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

/**
 * @param <T> builder
 * @author wuxii
 */
public abstract class SelectionBuilder<X, T extends SelectionBuilder> implements SelectionGenerator<X> {

    protected String alias;

    public T alias(String alias) {
        this.alias = alias;
        return (T) this;
    }

    @Override
    public final Column generate(Root<X> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return build(new QueryModel(root, query, cb));
    }

    protected abstract Column build(QueryModel model);

    protected static class QueryModel {

        private Root root;
        private CriteriaQuery<?> query;
        private CriteriaBuilder criteriaBuilder;

        public QueryModel(Root root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            this.root = root;
            this.query = query;
            this.criteriaBuilder = criteriaBuilder;
        }

        Expression get(String name) {
            return null;
        }

        public Root getRoot() {
            return root;
        }

        public CriteriaQuery<?> getQuery() {
            return query;
        }

        public CriteriaBuilder getCriteriaBuilder() {
            return criteriaBuilder;
        }
    }

}

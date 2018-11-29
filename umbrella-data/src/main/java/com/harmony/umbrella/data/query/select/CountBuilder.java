package com.harmony.umbrella.data.query.select;

import com.harmony.umbrella.data.query.Column;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * @author wuxii
 */
public class CountBuilder<X> extends SelectionBuilder<X, CountBuilder<X>> {

    private String name;

    private boolean distinct;

    public CountBuilder() {
    }

    public CountBuilder(String name) {
        this.name = name;
    }

    @Override
    public Column generate(Root<X> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return null;
    }

    public CountBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public CountBuilder setDistinct(boolean distinct) {
        this.distinct = distinct;
        return this;
    }
}

package com.harmony.umbrella.data.model;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

public class RootExpressionModel implements ExpressionModel {

    private Root<?> root;
    private CriteriaBuilder cb;

    public RootExpressionModel(Root<?> root, CriteriaBuilder cb) {
        this.root = root;
        this.cb = cb;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Path<?> getFrom() {
        return root;
    }

    @Override
    public Expression<?> getExpression() {
        return root;
    }

    @Override
    public ExpressionModel previous() {
        return null;
    }

    @Override
    public ExpressionModel next(String name) {
        return new StringExpressionModel(this, root, cb, name);
    }

    @Override
    public boolean isFunction() {
        return false;
    }

}

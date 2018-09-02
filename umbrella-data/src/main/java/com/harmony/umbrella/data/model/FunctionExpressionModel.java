package com.harmony.umbrella.data.model;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;

public class FunctionExpressionModel implements ExpressionModel {

    private ExpressionModel parent;
    private Path from;
    private CriteriaBuilder cb;
    private String function;

    FunctionExpressionModel(ExpressionModel parent, Path from, String function, CriteriaBuilder cb) {
        this.parent = parent;
        this.from = from;
        this.function = function;
        this.cb = cb;
    }

    @Override
    public String getName() {
        return function;
    }

    @Override
    public Path<?> getFrom() {
        return from;
    }

    @Override
    public Expression<?> toExpression() {
        return cb.function(function, null, from);
    }

    @Override
    public ExpressionModel previous() {
        return parent;
    }

    @Override
    public ExpressionModel next(String name) {
        return null;
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public boolean canJoin() {
        return false;
    }

}
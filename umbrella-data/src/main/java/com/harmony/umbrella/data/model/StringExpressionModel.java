package com.harmony.umbrella.data.model;

import com.harmony.umbrella.data.QueryException;

import javax.persistence.criteria.*;

public class StringExpressionModel implements ExpressionModel {

    private ExpressionModel parent;
    private JoinType defaultJoinType;

    private CriteriaBuilder cb;
    private String name;

    StringExpressionModel(ExpressionModel parent, CriteriaBuilder cb, String name) {
        this(parent, JoinType.LEFT, cb, name);
    }

    private StringExpressionModel(ExpressionModel parent, JoinType defaultJoinType, CriteriaBuilder cb, String name) {
        this.parent = parent;
        this.defaultJoinType = defaultJoinType;
        this.cb = cb;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Path<?> getExpression() {
        return getFrom().get(name);
    }

    public Join<?, ?> getJoinExpression(JoinType joinType) {
        Path<?> from = getFrom();
        if (!(from instanceof From)
                || !QueryModel.requiresJoin((From<?, ?>) from, name)) {
            throw new QueryException("Can't join " + from);
        }
        return ((From<?, ?>) from)
                .getJoins()
                .stream()
                .filter(e -> e.getAttribute().getName().equals(name))
                .filter(e -> joinType == null || e.getJoinType() == joinType)
                .findAny()
                .orElse(((From) from).join(name, JoinType.LEFT));
    }

    @Override
    public ExpressionModel previous() {
        return parent;
    }

    @Override
    public ExpressionModel next(String name) {
        return next(name, defaultJoinType);
    }

    public ExpressionModel next(String name, JoinType joinType) {
        return new StringExpressionModel(this, joinType, cb, name);
    }

    @Override
    public boolean isFunction() {
        return false;
    }

    private Path<?> getFrom() {
        Path from;
        if (parent instanceof StringExpressionModel) {
            from = ((StringExpressionModel) parent).getJoinExpression(defaultJoinType);
        } else {
            Expression expression = parent.getExpression();
            if (!(expression instanceof Path)) {
                throw new QueryException("parent model expression not path");
            }
            from = (Path) expression;
        }
        return from;
    }

}

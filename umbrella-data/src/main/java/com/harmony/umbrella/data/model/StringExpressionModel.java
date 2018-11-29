package com.harmony.umbrella.data.model;

import com.harmony.umbrella.data.query.QueryException;

import javax.persistence.criteria.*;

import static com.harmony.umbrella.data.model.RootModel.requiresJoin;

public class StringExpressionModel implements ExpressionModel {

    private ExpressionModel parent;
    private CriteriaBuilder cb;

    private Path from;
    private String name;

    private StringExpressionModel(ExpressionModel parent, CriteriaBuilder cb, String name) {
        this(parent, (Path) parent.getExpression(), cb, name);
    }

    StringExpressionModel(ExpressionModel parent, Path from, CriteriaBuilder cb, String name) {
        this.parent = parent;
        this.from = from;
        this.cb = cb;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Path<?> getFrom() {
        return from;
    }

    @Override
    public Path<?> getExpression() {
        return from.get(name);
    }

    public Join<?, ?> toJoinExpression(JoinType joinType) {
        if (!canJoin(from)) {
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
        return new StringExpressionModel(this, cb, name);
    }

    public ExpressionModel next(String name, JoinType joinType) {
        return new StringExpressionModel(this, toJoinExpression(joinType), cb, name);
    }

    public FunctionExpressionModel asFunction(String function) {
        return new FunctionExpressionModel(this, getExpression(), function, cb);
    }

    @Override
    public boolean isFunction() {
        return false;
    }

    private boolean canJoin(Expression exp) {
        return exp instanceof From && requiresJoin((From<?, ?>) exp, name);
    }

}

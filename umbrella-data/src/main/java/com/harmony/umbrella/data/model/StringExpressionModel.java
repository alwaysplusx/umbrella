package com.harmony.umbrella.data.model;

import com.harmony.umbrella.data.query.QueryException;

import javax.persistence.criteria.*;

import static com.harmony.umbrella.data.model.RootModel.requiresJoin;

public class StringExpressionModel implements ExpressionModel {

    private ExpressionModel parent;
    private CriteriaBuilder cb;

    private Path from;
    private String name;

    StringExpressionModel(ExpressionModel parent, CriteriaBuilder cb, String name) {
        this(parent, (Path) parent.toExpression(), cb, name);
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
    public Path<?> toExpression() {
        return from.get(name);
    }

    public Join<?, ?> toJoinExpression() {
        return toJoinExpression(null);
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
        return new StringExpressionModel(this, toJoinExpression(), cb, name);
    }

    public FunctionExpressionModel asFunction(String function) {
        return new FunctionExpressionModel(this, (Path) toExpression(), function, cb);
    }

    public FunctionExpressionModel asFunction(String function, JoinType joinType) {
        return new FunctionExpressionModel(this, (Path) toJoinExpression(joinType), function, cb);
    }

    @Override
    public boolean isFunction() {
        return false;
    }

    @Override
    public boolean canJoin() {
        return canJoin(from);
    }

    private boolean canJoin(Expression exp) {
        return exp instanceof From && requiresJoin((From<?, ?>) exp, name);
    }

}

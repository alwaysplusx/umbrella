package com.harmony.umbrella.data.model;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

/**
 * @author wuxii
 */
public interface ExpressionModel {

    default String getPath() {
        if (!hasPrevious()) {
            return getName();
        }
        String path = previous().getPath();
        return isFunction()
                ? getName() + "(" + path + ")"
                : path != null ? path + "." + getName() : getName();
    }

    String getName();

    Path<?> getFrom();

    Expression toExpression();

    ExpressionModel previous();

    ExpressionModel next(String name);

    boolean isFunction();

    boolean canJoin();

    default boolean hasPrevious() {
        return previous() != null;
    }

    default boolean isRoot() {
        return getFrom() instanceof Root;
    }

}

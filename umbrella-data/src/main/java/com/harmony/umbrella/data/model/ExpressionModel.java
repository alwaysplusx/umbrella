package com.harmony.umbrella.data.model;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.util.List;

/**
 * @author wuxii
 */
public interface ExpressionModel extends Selection {

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

    @Override
    default Selection alias(String name) {
        return toExpression().alias(name);
    }

    @Override
    default boolean isCompoundSelection() {
        return toExpression().isCompoundSelection();
    }

    @Override
    default List<Selection<?>> getCompoundSelectionItems() {
        return toExpression().getCompoundSelectionItems();
    }

    @Override
    default Class getJavaType() {
        return toExpression().getJavaType();
    }

    @Override
    default String getAlias() {
        return toExpression().getAlias();
    }

}

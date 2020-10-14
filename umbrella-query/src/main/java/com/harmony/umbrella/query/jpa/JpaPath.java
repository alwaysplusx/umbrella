package com.harmony.umbrella.query.jpa;

import com.harmony.umbrella.query.Path;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

public interface JpaPath<T> extends Path<T> {

    default Expression<?> getExpression(Root<T> root) {
        return PathExpressionResolver.defaultResolver.resolve(this, root);
    }

}

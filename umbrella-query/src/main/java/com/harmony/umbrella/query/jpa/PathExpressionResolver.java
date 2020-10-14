package com.harmony.umbrella.query.jpa;

import com.harmony.umbrella.query.Path;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

public interface PathExpressionResolver {

    PathExpressionResolver defaultResolver = new PathExpressionResolver() {
        public <T> Expression<?> resolve(Path<T> path, Root<T> root) {
            return root.get(path.getColumn());
        }
    };

    <T> Expression<?> resolve(Path<T> path, Root<T> root);

}

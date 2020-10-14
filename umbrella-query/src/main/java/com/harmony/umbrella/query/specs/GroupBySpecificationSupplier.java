package com.harmony.umbrella.query.specs;

import com.harmony.umbrella.query.Path;
import com.harmony.umbrella.query.SpecificationSupplier;
import com.harmony.umbrella.query.jpa.JpaPath;
import com.harmony.umbrella.query.jpa.PathExpressionResolver;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Expression;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupBySpecificationSupplier<T> implements SpecificationSupplier<T> {

    private List<Path<T>> columns = Collections.emptyList();

    private PathExpressionResolver pathExpressionResolver = PathExpressionResolver.defaultResolver;

    @Override
    public Specification<T> get() {
        return (Specification<T>) (root, query, cb) -> {
            List<Expression<?>> expressions = new ArrayList<>();
            for (Path<T> column : columns) {
                if (column instanceof JpaPath) {
                    expressions.add(((JpaPath<T>) column).getExpression(root));
                } else {
                    expressions.add(pathExpressionResolver.resolve(column, root));
                }
            }
            query.groupBy(expressions);
            return null;
        };
    }

}

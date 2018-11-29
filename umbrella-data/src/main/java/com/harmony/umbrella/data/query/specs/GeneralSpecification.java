package com.harmony.umbrella.data.query.specs;

import com.harmony.umbrella.data.ExpressionOperator;
import com.harmony.umbrella.data.model.RootModel;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author wuxii
 */
public interface GeneralSpecification<T> extends Specification<T> {

    Predicate toPredicate(RootModel<T> rootModel, CriteriaQuery<?> query, CriteriaBuilder builder);

    default Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        return toPredicate(RootModel.of(root, builder), query, builder);
    }

    static GeneralSpecification of(String name, Object value, ExpressionOperator operator) {
        return (root, query, builder) -> operator.explain(root.get(name), value, builder);
    }

    static <T> GeneralSpecification<T> between(String name, Comparable left, Comparable right) {
        return (root, query, builder) -> builder.between(root.get(name).getExpression(), left, right);
    }

    static <T> GeneralSpecification<T> notBetween(String name, Comparable left, Comparable right) {
        return (root, query, builder) -> builder.between(root.get(name).getExpression(), left, right).not();
    }

}

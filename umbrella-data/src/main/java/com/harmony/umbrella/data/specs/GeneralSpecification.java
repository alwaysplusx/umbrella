package com.harmony.umbrella.data.specs;

import com.harmony.umbrella.data.ExpressionOperator;
import com.harmony.umbrella.data.model.QueryModel;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author wuxii
 */
public interface GeneralSpecification<T> extends Specification<T> {

    Predicate toPredicate(QueryModel queryModel);

    default Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        return toPredicate(new QueryModel(root, query, builder));
    }

    static GeneralSpecification of(String name, Object value, ExpressionOperator operator) {
        return q -> operator.explain(q.recursiveGet(name), value, q.getCriteriaBuilder());
    }

    static <T> GeneralSpecification<T> between(String name, Comparable left, Comparable right) {
        return q -> q.getCriteriaBuilder().between(q.get(name), left, right);
    }

    static <T> GeneralSpecification<T> notBetween(String name, Comparable left, Comparable right) {
        return q -> q.getCriteriaBuilder().between(q.get(name), left, right).not();
    }

}

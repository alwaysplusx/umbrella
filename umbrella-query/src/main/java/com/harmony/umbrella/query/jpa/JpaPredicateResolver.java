package com.harmony.umbrella.query.jpa;

import com.harmony.umbrella.query.CriteriaDefinition.Comparator;
import com.harmony.umbrella.query.Path;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class JpaPredicateResolver {

    private CriteriaBuilder criteriaBuilder;
    private Root<?> root;

    public Predicate resolve(Path<?> path, Object value, Comparator comparator) {

        return null;
    }

}

package com.harmony.umbrella.data;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

/**
 * Enum for the composition types for {@link Predicate}s.
 * 
 * @author Thomas Darimont
 */
public enum CompositionType {

    AND("and") {

        @Override
        public <T> Specification<T> combine(Specification<T> lhs, Specification<T> rhs) {
            return Specifications.where(lhs).and(rhs);
        }

        @Override
        public Predicate combine(CriteriaBuilder builder, Predicate lhs, Predicate rhs) {
            return builder.and(lhs, rhs);
        }

    },

    OR("or") {

        @Override
        public <T> Specification<T> combine(Specification<T> lhs, Specification<T> rhs) {
            return Specifications.where(lhs).or(rhs);
        }

        @Override
        public Predicate combine(CriteriaBuilder builder, Predicate lhs, Predicate rhs) {
            return builder.or(lhs, rhs);
        }

    };

    public abstract <T> Specification<T> combine(Specification<T> lhs, Specification<T> rhs);

    public abstract Predicate combine(CriteriaBuilder builder, Predicate lhs, Predicate rhs);

    private String qualifiedName;

    private CompositionType(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public String qualifiedName() {
        return qualifiedName;
    }
}
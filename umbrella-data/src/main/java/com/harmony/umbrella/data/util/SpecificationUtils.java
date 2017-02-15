package com.harmony.umbrella.data.util;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

/**
 * @author wuxii@foxmail.com
 */
public class SpecificationUtils {

    public static <T> Specification<T> conjunction() {
        return new SignalSpecification<T>(true);
    }

    public static <T> Specification<T> disjunction() {
        return new SignalSpecification<T>(false);
    }

    private static final class SignalSpecification<T> implements Specification<T> {

        private boolean signal;

        private SignalSpecification(boolean signal) {
            this.signal = signal;
        }

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return signal ? cb.conjunction() : cb.disjunction();
        }

    }
}

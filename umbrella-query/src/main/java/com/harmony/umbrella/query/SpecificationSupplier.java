package com.harmony.umbrella.query;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.function.Supplier;

public interface SpecificationSupplier<T> extends Supplier<Specification<T>> {

    @SafeVarargs
    static <T> SpecificationSupplier<T> all(SpecificationSupplier<T>... spec) {
        return () -> (Specification<T>) (root, query, cb) -> {
            Predicate predicate = null;
            for (SpecificationSupplier<T> ss : spec) {
                Predicate temp = ss.get().toPredicate(root, query, cb);
                predicate = (predicate == null)
                        ? temp
                        : temp != null ? cb.and(predicate, temp) : predicate;
            }
            return predicate;
        };
    }

    static <T> SpecificationSupplier<T> empty() {
        return () -> (Specification<T>) (root, query, criteriaBuilder) -> null;
    }

    /**
     * 使用此方法后将主动丢弃{@link Specification#toPredicate(Root, CriteriaQuery, CriteriaBuilder)}返回结果
     */
    static <T> SpecificationSupplier<T> none(SpecificationSupplier<T> spec) {
        return () -> (Specification<T>) (root, query, cb) -> {
            spec.get().toPredicate(root, query, cb);
            return null;
        };
    }

    static <T> SpecificationSupplier<T> of(Specification<T> spec) {
        return () -> spec;
    }

    @NotNull
    @Override
    Specification<T> get();

}

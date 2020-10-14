package com.harmony.umbrella.query.specs;

import com.harmony.umbrella.query.SpecificationSupplier;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

import javax.persistence.criteria.Predicate;

public class HavingSpecificationSupplier<T> implements SpecificationSupplier<T> {

    private SpecificationSupplier<T> specificationSupplier;

    public HavingSpecificationSupplier(SpecificationSupplier<T> specificationSupplier) {
        Assert.notNull(specificationSupplier, "having condition not allow null");
        this.specificationSupplier = specificationSupplier;
    }

    @Override
    public Specification<T> get() {
        return (Specification<T>) (root, query, cb) -> {
            Predicate predicate = specificationSupplier.get().toPredicate(root, query, cb);
            query.having(predicate);
            return null;
        };
    }

}

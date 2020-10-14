package com.harmony.umbrella.query.specs;

import com.harmony.umbrella.query.SpecificationSupplier;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

public class NullSpecificationSupplier<T> implements SpecificationSupplier<T> {

    private SpecificationSupplier<T> specificationSupplier;

    @NotNull
    @Override
    public Specification<T> get() {
        return (Specification<T>) (root, query, cb) -> {
            specificationSupplier.get().toPredicate(root, query, cb);
            return null;
        };
    }

}

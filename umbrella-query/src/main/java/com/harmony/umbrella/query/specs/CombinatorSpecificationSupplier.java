package com.harmony.umbrella.query.specs;

import com.harmony.umbrella.query.CriteriaDefinition.Combinator;
import com.harmony.umbrella.query.SpecificationSupplier;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

public class CombinatorSpecificationSupplier<T> implements SpecificationSupplier<T> {

    private SpecificationSupplier<T> left;
    private SpecificationSupplier<T> right;
    private Combinator combinator;

    public CombinatorSpecificationSupplier(SpecificationSupplier<T> left, SpecificationSupplier<T> right, Combinator combinator) {
        this.left = left;
        this.right = right;
        this.combinator = combinator;
    }

    @NotNull
    @Override
    public Specification<T> get() {
        switch (combinator) {
            case INITIAL:
                return Specification.where(left == null ? right.get() : left.get());
            case AND:
                return Specification.where(left.get()).and(right.get());
            case OR:
                return Specification.where(left.get()).or(right.get());
        }
        throw new IllegalStateException("unknown combinator " + combinator);
    }

}

package com.harmony.umbrella.query;

import org.springframework.data.domain.Sort;

import java.util.function.Supplier;

public interface SortSupplier extends Supplier<Sort> {

    static SortSupplier and(SortSupplier left, SortSupplier right) {
        return () -> left.get().and(right.get());
    }

}

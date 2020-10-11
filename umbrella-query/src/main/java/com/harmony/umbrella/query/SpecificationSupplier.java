package com.harmony.umbrella.query;

import org.springframework.data.jpa.domain.Specification;

import java.util.function.Supplier;

public interface SpecificationSupplier<T> extends Supplier<Specification<T>> {

}

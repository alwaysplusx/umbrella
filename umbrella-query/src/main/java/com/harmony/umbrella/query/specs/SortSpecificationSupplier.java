package com.harmony.umbrella.query.specs;

import com.harmony.umbrella.query.SortSupplier;
import com.harmony.umbrella.query.SpecificationSupplier;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;

import javax.persistence.criteria.Order;
import java.util.List;

public class SortSpecificationSupplier<T> implements SpecificationSupplier<T> {

    private SortSupplier sortSupplier;

    public SortSpecificationSupplier(SortSupplier sortSupplier) {
        this.sortSupplier = sortSupplier;
    }

    @NotNull
    @Override
    public Specification<T> get() {
        return (Specification<T>) (root, query, cb) -> {
            List<Order> orders = QueryUtils.toOrders(sortSupplier.get(), root, cb);
            query.orderBy(orders);
            return null;
        };
    }

}

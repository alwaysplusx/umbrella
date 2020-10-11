package com.harmony.umbrella.query.jpa;

import com.harmony.umbrella.query.*;
import com.harmony.umbrella.query.CriteriaDefinition.Combinator;
import com.harmony.umbrella.query.CriteriaDefinition.Comparator;
import com.harmony.umbrella.query.specs.CombinatorSpecificationSupplier;
import com.harmony.umbrella.query.specs.PathSpecificationSupplier;
import org.springframework.data.jpa.domain.Specification;
import sun.plugin.com.event.COMEventHandler;

public class JpaCriteriaBuilder<DOMAIN> implements CriteriaBuilder<DOMAIN, JpaCriteriaBuilder<DOMAIN>>, SpecificationSupplier<DOMAIN> {

    private Combinator combinator = Combinator.INITIAL;
    private SpecificationSupplier<DOMAIN> specificationSupplier;
    private PathFunctionResolver pathFunctionResolver;

    public JpaCriteriaBuilder() {
        this(new PathFunctionResolver());
    }

    public JpaCriteriaBuilder(PathFunctionResolver pathFunctionResolver) {
        this.pathFunctionResolver = pathFunctionResolver;
    }

    private JpaCriteriaBuilder(PathFunctionResolver pathFunctionResolver,
                               SpecificationSupplier<DOMAIN> specificationSupplier,
                               Combinator combinator) {
        this.pathFunctionResolver = pathFunctionResolver;
        this.specificationSupplier = specificationSupplier;
        this.combinator = combinator;
    }

    @Override
    public <T> JpaCriteriaBuilder<DOMAIN> equal(Path<DOMAIN> column, Object value) {
        return nextBuilder(newCriteria(column, value, Comparator.EQ), combinator);
    }

    @Override
    public <T> JpaCriteriaBuilder<DOMAIN> equal(PathFunction<DOMAIN, T> column, T value) {
        return nextBuilder(newCriteria(column, value, Comparator.EQ), combinator);
    }

    public JpaCriteriaBuilder<DOMAIN> and() {
        return newBuilder(Combinator.AND);
    }

    public JpaCriteriaBuilder<DOMAIN> and(SpecificationSupplier<DOMAIN> spec) {
        return nextBuilder(spec, Combinator.AND);
    }

    public JpaCriteriaBuilder<DOMAIN> and(Specification<DOMAIN> spec) {
        return nextBuilder(() -> spec, Combinator.AND);
    }

    public JpaCriteriaBuilder<DOMAIN> or() {
        return newBuilder(Combinator.OR);
    }

    public JpaCriteriaBuilder<DOMAIN> or(SpecificationSupplier<DOMAIN> spec) {
        return nextBuilder(spec, Combinator.OR);
    }

    public JpaCriteriaBuilder<DOMAIN> or(Specification<DOMAIN> spec) {
        return nextBuilder(() -> spec, Combinator.OR);
    }

    protected <T> SpecificationSupplier<DOMAIN> newCriteria(PathFunction<DOMAIN, T> column,
                                                            Object value,
                                                            Comparator comparator) {
        return newCriteria(pathFunctionResolver.resolve(column), value, comparator);
    }

    protected <T> SpecificationSupplier<DOMAIN> newCriteria(Path<DOMAIN> column,
                                                            Object value,
                                                            Comparator comparator) {
        return new PathSpecificationSupplier<>(column, value, comparator);
    }

    @Override
    public Specification<DOMAIN> get() {
        return specificationSupplier == null ? Specification.where(null) : specificationSupplier.get();
    }

    protected JpaCriteriaBuilder<DOMAIN> newBuilder(Combinator combinator) {
        return new JpaCriteriaBuilder<>(pathFunctionResolver, this.specificationSupplier, combinator);
    }

    protected JpaCriteriaBuilder<DOMAIN> nextBuilder(SpecificationSupplier<DOMAIN> spec, Combinator combinator) {
        SpecificationSupplier<DOMAIN> nextSpec = new CombinatorSpecificationSupplier<>(this.specificationSupplier, spec, combinator);
        return new JpaCriteriaBuilder<>(pathFunctionResolver, nextSpec, Combinator.AND);
    }

}

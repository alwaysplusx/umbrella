package com.harmony.umbrella.query.jpa;

import com.harmony.umbrella.query.QueryBuilder;
import com.harmony.umbrella.query.SortSupplier;
import com.harmony.umbrella.query.SpecificationSupplier;
import com.harmony.umbrella.query.jpa.factory.JpaCriteriaBuilderFactory;
import com.harmony.umbrella.query.jpa.factory.JpaSortBuilderFactory;
import com.harmony.umbrella.query.result.QueryResult;
import com.harmony.umbrella.query.specs.SortSpecificationSupplier;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import java.util.function.Function;

import static com.harmony.umbrella.query.SpecificationSupplier.none;

public class JpaQueryBuilder<DOMAIN> implements QueryBuilder<DOMAIN, JpaQueryBuilder<DOMAIN>> {

    public static <T> JpaQueryBuilder<T> from(Class<T> domainClass) {
        return new JpaQueryBuilder<>(domainClass);
    }

    protected JpaCriteriaBuilderFactory criteriaBuilderFactory = new JpaCriteriaBuilderFactoryImpl();

    protected JpaSortBuilderFactory sortBuilderFactory = JpaSortBuilder::new;

    protected final Class<DOMAIN> domainClass;

    protected EntityManager entityManager;

    protected SpecificationSupplier<DOMAIN> specification = SpecificationSupplier.empty();

    private JpaQueryBuilder(Class<DOMAIN> domainClass) {
        this.domainClass = domainClass;
    }

    public JpaQueryBuilder<DOMAIN> withEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        return this;
    }

    public JpaQueryBuilder<DOMAIN> where(Specification<DOMAIN> spec) {
        return nextBuilder(SpecificationSupplier.of(spec));
    }

    public JpaQueryBuilder<DOMAIN> where(SpecificationSupplier<DOMAIN> spec) {
        return nextBuilder(spec);
    }

    public JpaQueryBuilder<DOMAIN> where(Function<JpaCriteriaBuilder<DOMAIN>, SpecificationSupplier<DOMAIN>> fun) {
        JpaCriteriaBuilder<DOMAIN> builder = criteriaBuilderFactory.newCriteriaBuilder(domainClass);
        return nextBuilder(fun.apply(builder));
    }

    public JpaQueryBuilder<DOMAIN> having(Function<JpaCriteriaBuilder<DOMAIN>, SpecificationSupplier<DOMAIN>> fun) {
        JpaCriteriaBuilder<DOMAIN> builder = criteriaBuilderFactory.newCriteriaBuilder(domainClass);
        return nextBuilder(none(fun.apply(builder)));
    }

    public JpaQueryBuilder<DOMAIN> orderBy(Function<JpaSortBuilder<DOMAIN>, SortSupplier> fun) {
        JpaSortBuilder<DOMAIN> sortBuilder = sortBuilderFactory.newSortBuilder();
        SortSupplier sortSupplier = fun.apply(sortBuilder);
        return nextBuilder(none(new SortSpecificationSupplier<>(sortSupplier)));
    }

    protected JpaQueryBuilder<DOMAIN> nextBuilder(SpecificationSupplier<DOMAIN> nextSpec) {
        return newBuilder(SpecificationSupplier.all(specification, nextSpec));
    }

    protected JpaQueryBuilder<DOMAIN> newBuilder(SpecificationSupplier<DOMAIN> spec) {
        JpaQueryBuilder<DOMAIN> nextBuilder = new JpaQueryBuilder<>(this.domainClass);
        nextBuilder.specification = spec;
        nextBuilder.sortBuilderFactory = this.sortBuilderFactory;
        nextBuilder.entityManager = this.entityManager;
        nextBuilder.criteriaBuilderFactory = this.criteriaBuilderFactory;
        return nextBuilder;
    }

    public QueryResult<DOMAIN> execute() {
        if (entityManager == null) {
            throw new IllegalStateException("query result required entity manager");
        }
        return new QueryResult<DOMAIN>(entityManager)
                .setDomainClass(domainClass)
                .setSpecification(specification);
    }

    private static class JpaCriteriaBuilderFactoryImpl implements com.harmony.umbrella.query.jpa.factory.JpaCriteriaBuilderFactory {
        @Override
        public <T> JpaCriteriaBuilder<T> newCriteriaBuilder(Class<T> domainClass) {
            return new JpaCriteriaBuilder<>();
        }
    }

}

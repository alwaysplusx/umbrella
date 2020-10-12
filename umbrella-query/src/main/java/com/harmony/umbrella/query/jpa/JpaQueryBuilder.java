package com.harmony.umbrella.query.jpa;

import com.harmony.umbrella.query.CriteriaDefinition.Combinator;
import com.harmony.umbrella.query.QueryBuilder;
import com.harmony.umbrella.query.SortSupplier;
import com.harmony.umbrella.query.SpecificationSupplier;
import com.harmony.umbrella.query.jpa.factory.JpaCriteriaBuilderFactory;
import com.harmony.umbrella.query.jpa.factory.JpaSortBuilderFactory;
import com.harmony.umbrella.query.result.QueryResult;
import com.harmony.umbrella.query.specs.CombinatorSpecificationSupplier;
import com.harmony.umbrella.query.specs.SortSpecificationSupplier;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import java.util.function.Function;

public class JpaQueryBuilder<DOMAIN> implements QueryBuilder<DOMAIN, JpaQueryBuilder<DOMAIN>> {

    public static <T> JpaQueryBuilder<T> from(Class<T> domainClass) {
        return new JpaQueryBuilder<>(domainClass);
    }

    protected JpaCriteriaBuilderFactory criteriaBuilderFactory = new JpaCriteriaBuilderFactory() {
        @Override
        public <T> JpaCriteriaBuilder<T> newCriteriaBuilder(Class<T> domainClass) {
            return new JpaCriteriaBuilder<>();
        }
    };

    protected JpaSortBuilderFactory sortBuilderFactory = JpaSortBuilder::new;

    protected final Class<DOMAIN> domainClass;

    protected EntityManager entityManager;

    protected SpecificationSupplier<DOMAIN> specification;

    private JpaQueryBuilder(Class<DOMAIN> domainClass) {
        this.domainClass = domainClass;
    }

    public JpaQueryBuilder<DOMAIN> withEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        return this;
    }

    public JpaQueryBuilder<DOMAIN> where(Specification<DOMAIN> spec) {
        this.specification = () -> spec;
        return this;
    }

    public JpaQueryBuilder<DOMAIN> where(SpecificationSupplier<DOMAIN> spec) {
        this.specification = spec;
        return this;
    }

    public JpaQueryBuilder<DOMAIN> where(Function<JpaCriteriaBuilder<DOMAIN>, SpecificationSupplier<DOMAIN>> fun) {
        JpaCriteriaBuilder<DOMAIN> builder = criteriaBuilderFactory.newCriteriaBuilder(domainClass);
        return where(fun.apply(builder));
    }

    public JpaQueryBuilder<DOMAIN> orderBy(Function<JpaSortBuilder<DOMAIN>, SortSupplier> fun) {
        JpaSortBuilder<DOMAIN> sortBuilder = sortBuilderFactory.newSortBuilder();
        SortSupplier sortSupplier = fun.apply(sortBuilder);
        SpecificationSupplier<DOMAIN> sortSpec = new SortSpecificationSupplier<>(sortSupplier);
        return where(new CombinatorSpecificationSupplier<>(specification, sortSpec, Combinator.AND));
    }

    public QueryResult<DOMAIN> execute() {
        if (entityManager == null) {
            throw new IllegalStateException("query result required entity manager");
        }
        return new QueryResult<DOMAIN>(entityManager)
                .setDomainClass(domainClass)
                .setSpecification(specification);
    }

}

package com.harmony.umbrella.query.jpa;

import com.harmony.umbrella.query.QueryBuilder;
import com.harmony.umbrella.query.SpecificationSupplier;
import com.harmony.umbrella.query.result.QueryResult;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import java.util.function.Function;

public class JpaQueryBuilder<DOMAIN> implements QueryBuilder<DOMAIN, JpaQueryBuilder<DOMAIN>> {

    public static <T> JpaQueryBuilder<T> from(Class<T> domainClass) {
        return new JpaQueryBuilder<>(domainClass);
    }

    protected JpaCriteriaBuilderFactory criteriaBuilderFactory = new JpaCriteriaBuilderFactoryImpl();

    protected final Class<DOMAIN> domainClass;

    protected EntityManager entityManager;

    protected Specification<DOMAIN> specification;

    private JpaQueryBuilder(Class<DOMAIN> domainClass) {
        this.domainClass = domainClass;
    }

    public JpaQueryBuilder<DOMAIN> withEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        return this;
    }

    public JpaQueryBuilder<DOMAIN> where(Specification<DOMAIN> spec) {
        this.specification = spec;
        return this;
    }

    public QueryResult<DOMAIN> execute() {
        if (entityManager == null) {
            throw new IllegalStateException("query result required entity manager");
        }
        return new QueryResult<DOMAIN>(entityManager)
                .setDomainClass(domainClass)
                .setSpecification(specification);
    }

    public JpaQueryBuilder<DOMAIN> where(Function<JpaCriteriaBuilder<DOMAIN>, SpecificationSupplier<DOMAIN>> fun) {
        JpaCriteriaBuilder<DOMAIN> builder = criteriaBuilderFactory.newCriteriaBuilder(domainClass);
        Specification<DOMAIN> spec = fun.apply(builder).get();
        return where(spec);
    }

    private static class JpaCriteriaBuilderFactoryImpl implements JpaCriteriaBuilderFactory {

        @Override
        public <T> JpaCriteriaBuilder<T> newCriteriaBuilder(Class<T> domainClass) {
            return new JpaCriteriaBuilder<>();
        }

    }

}

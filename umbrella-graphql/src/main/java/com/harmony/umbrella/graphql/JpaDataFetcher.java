package com.harmony.umbrella.graphql;

import com.harmony.umbrella.data.JpaQueryBuilder;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;

/**
 * @author wuxin
 */
@Slf4j
public class JpaDataFetcher implements DataFetcher<Object> {

    private EntityManager entityManager;

    public JpaDataFetcher() {

    }

    public JpaDataFetcher(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Object get(DataFetchingEnvironment environment) {
        FetchContext fetchContext = FetchContext.of(environment);
        JpaQueryBuilder<?> builder = queryBuilder(fetchContext.getRootClass());
        for (FetchContext.FetchArgument argument : fetchContext.getArguments()) {
            GraphQLArgument argumentDefinition = argument.getArgumentDefinition();
            String argumentName = argumentDefinition.getName();
            Object value = argument.getValue();
            if (value != null) {
                builder.addCondition(argumentName, value, argument.getCondition());
            }
        }
        // FIXME result list or page?
        return builder.getSingleResult();
    }

    private <T> JpaQueryBuilder<T> queryBuilder(Class<T> domainClass) {
        return JpaQueryBuilder.newBuilder(domainClass);
    }

}

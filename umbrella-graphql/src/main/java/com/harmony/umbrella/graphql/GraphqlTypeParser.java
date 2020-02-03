package com.harmony.umbrella.graphql;

import graphql.schema.GraphQLType;

/**
 * @author wuxin
 */
public interface GraphqlTypeParser {

    boolean support(Class<?> clazz);

    GraphQLType parse(Class<?> clazz);

}

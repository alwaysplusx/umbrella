package com.harmony.umbrella.graphql.parser;

import com.harmony.umbrella.graphql.GraphqlTypeParser;
import graphql.schema.GraphQLType;

/**
 * @author wuxin
 */
public class AnyGraphqlTypeParser implements GraphqlTypeParser {

    @Override
    public boolean support(Class<?> clazz) {
        return false;
    }

    @Override
    public GraphQLType parse(Class<?> clazz) {
        return null;
    }

}

package com.harmony.umbrella.graphql.parser;

import com.harmony.umbrella.graphql.GraphqlTypeParser;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLType;

import java.util.Collection;

/**
 * @author wuxin
 */
public class ArrayGraphqlTypeParser implements GraphqlTypeParser {

    @Override
    public boolean support(Class<?> clazz) {
        return clazz.isArray() || Collection.class.isAssignableFrom(clazz);
    }

    @Override
    public GraphQLType parse(Class<?> clazz) {
        return GraphQLList.list(null);
    }
}

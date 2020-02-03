package com.harmony.umbrella.graphql.parser;

import com.harmony.umbrella.graphql.GraphqlTypeParser;
import com.harmony.umbrella.graphql.utils.GraphqlUtils;
import graphql.schema.GraphQLType;

/**
 * @author wuxin
 */
public class PrimitiveGraphqlObjectParser implements GraphqlTypeParser {

    @Override
    public boolean support(Class<?> clazz) {
        return GraphqlUtils.isPrimitiveType(clazz);
    }

    @Override
    public GraphQLType parse(Class<?> clazz) {
        return GraphqlUtils.primitiveType(clazz);
    }
}

package com.harmony.umbrella.graphql.parser;

import com.harmony.umbrella.graphql.GraphqlTypeParser;
import graphql.schema.GraphQLObjectType;
import org.springframework.data.domain.Pageable;

/**
 * @author wuxin
 */
public class PageableGraphqlObjectParser implements GraphqlTypeParser {

    @Override
    public boolean support(Class<?> clazz) {
        return Pageable.class.isAssignableFrom(clazz);
    }

    @Override
    public GraphQLObjectType parse(Class<?> clazz) {
        return GraphQLObjectType.newObject().build();
    }

}

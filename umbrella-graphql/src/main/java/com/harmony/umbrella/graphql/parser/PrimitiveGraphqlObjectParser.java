package com.harmony.umbrella.graphql.parser;

import com.harmony.umbrella.graphql.metadata.GraphqlFieldMetadata;
import com.harmony.umbrella.graphql.metadata.GraphqlParameterMetadata;
import com.harmony.umbrella.graphql.query.GraphqlInputParser;
import com.harmony.umbrella.graphql.query.GraphqlOutputParser;
import com.harmony.umbrella.graphql.utils.GraphqlUtils;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;

/**
 * @author wuxin
 */
public class PrimitiveGraphqlObjectParser implements GraphqlInputParser, GraphqlOutputParser {

    @Override
    public GraphQLInputType parse(GraphqlParameterMetadata parameterMetadata) {
        return null;
    }

    @Override
    public boolean support(GraphqlFieldMetadata fieldMetadata) {
        return GraphqlUtils.isPrimitiveType(fieldMetadata.getActualType());
    }

    @Override
    public GraphQLOutputType parse(GraphqlFieldMetadata metadata) {
        return null;
    }

}

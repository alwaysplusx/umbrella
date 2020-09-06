package com.harmony.umbrella.graphql.query;

import com.harmony.umbrella.graphql.metadata.GraphqlParameterMetadata;
import graphql.schema.GraphQLInputType;

/**
 * @author wuxin
 */
public interface GraphqlInputParser {

    GraphQLInputType parse(GraphqlParameterMetadata parameterMetadata);

}

package com.harmony.umbrella.graphql.query;

import com.harmony.umbrella.graphql.metadata.GraphqlFieldMetadata;
import graphql.schema.GraphQLOutputType;

public interface GraphqlOutputParser {

    boolean support(GraphqlFieldMetadata fieldMetadata);

    GraphQLOutputType parse(GraphqlFieldMetadata metadata);

}

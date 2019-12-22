package com.harmony.umbrella.graphql;

import com.harmony.umbrella.data.Operator;
import com.harmony.umbrella.graphql.metadata.GraphqlMethodMetadata;
import com.harmony.umbrella.graphql.metadata.GraphqlParameterMetadata;
import com.harmony.umbrella.graphql.type.MethodType;
import graphql.language.FieldDefinition;
import graphql.language.Type;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Builder(access = AccessLevel.PRIVATE)
public class FetchContext {

    private DataFetchingEnvironment environment;
    private GraphqlMethodMetadata methodMetadata;
    private List<FetchArgument> arguments;

    public static FetchContext of(DataFetchingEnvironment environment) {
        GraphQLFieldDefinition graphqlFieldDefinition = environment.getFieldDefinition();
        FieldDefinition fieldDefinition = graphqlFieldDefinition.getDefinition();
        Type fieldDefinitionType = fieldDefinition.getType();
        if (!(fieldDefinitionType instanceof MethodType)) {
            throw new GraphqlDataFetcherException("unsupported data fetcher field definition " + fieldDefinitionType);
        }
        Map<String, Object> argumentValueMap = environment.getArguments();
        Map<String, GraphQLArgument> argumentDefinitionMap = toArgumentMap(graphqlFieldDefinition.getArguments());
        GraphqlMethodMetadata methodMetadata = ((MethodType) fieldDefinitionType).getMethodMetadata();
        return FetchContext
                .builder()
                .environment(environment)
                .methodMetadata(methodMetadata)
                .arguments(
                        methodMetadata
                                .getParameters()
                                .stream()
                                .map(e -> new FetchArgument(
                                                e,
                                                argumentDefinitionMap.get(e.getName()),
                                                argumentValueMap.get(e.getName())
                                        )
                                )
                                .collect(Collectors.toList())
                )
                .build();
    }

    public Class<?> getRootClass() {
        return methodMetadata.getRootClass();
    }

    public GraphqlMethodMetadata getFetchMethod() {
        return methodMetadata;
    }

    public List<FetchArgument> getArguments() {
        return arguments;
    }

    private static Map<String, GraphQLArgument> toArgumentMap(List<GraphQLArgument> arguments) {
        return arguments
                .stream()
                .collect(Collectors.toMap(GraphQLArgument::getName, e -> e));
    }

    public static class FetchArgument {

        private GraphqlParameterMetadata parameterMetadata;
        private GraphQLArgument argumentDefinition;
        private Object value;

        public FetchArgument(GraphqlParameterMetadata parameterMetadata, GraphQLArgument argumentDefinition, Object value) {
            this.parameterMetadata = parameterMetadata;
            this.argumentDefinition = argumentDefinition;
            this.value = value;
        }

        public Operator getCondition() {
            return parameterMetadata.getCondition();
        }

        public Object getValue() {
            return value;
        }

        public GraphQLArgument getArgumentDefinition() {
            return argumentDefinition;
        }

    }

}

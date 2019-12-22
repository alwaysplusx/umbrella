package com.harmony.umbrella.graphql.metadata;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.harmony.umbrella.graphql.utils.GraphqlUtils.*;

@ToString
@Builder(access = AccessLevel.PACKAGE)
public class GraphqlMethodMetadata {

    public static GraphqlMethodMetadata of(Method method) {
        List<GraphqlParameterMetadata> parameters = Arrays.stream(method.getParameters())
                .filter(e -> !hasGraphqlIgnoreAnnotation(e))
                .map(GraphqlParameterMetadata::of)
                .collect(Collectors.toList());
        return GraphqlMethodMetadata
                .builder()
                .name(graphqlName(method))
                .method(method)
                .rootClass(fromClass(method))
                .parameters(parameters)
                .parameterMetadataMap(
                        parameters
                                .stream()
                                .collect(
                                        Collectors.toMap(
                                                GraphqlParameterMetadata::getName,
                                                e -> e
                                        )
                                )
                )
                .build();
    }

    @Getter
    String name;
    @Getter
    Method method;
    @Getter
    Class<?> rootClass;
    @Getter
    List<GraphqlParameterMetadata> parameters;

    Map<String, GraphqlParameterMetadata> parameterMetadataMap;

    public GraphqlParameterMetadata getParameterMetadata(String name) {
        return parameterMetadataMap.get(name);
    }

}
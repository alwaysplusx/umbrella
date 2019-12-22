package com.harmony.umbrella.graphql.metadata;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.harmony.umbrella.graphql.utils.GraphqlUtils.*;


/**
 * @author wuxin
 */
@Getter
@ToString
@Builder(access = AccessLevel.PACKAGE)
public class GraphqlMetadata {

    public static GraphqlMetadata of(Class<?> typeClass) {
        String graphqlName = graphqlName(typeClass);
        List<GraphqlFieldMetadata> fields = Arrays.stream(typeClass.getDeclaredFields())
                .filter(e -> !Modifier.isStatic(e.getModifiers()))
                .filter(e -> !hasGraphqlIgnoreAnnotation(e))
                .map(GraphqlFieldMetadata::of)
                .collect(Collectors.toList());
        boolean hasGraphqlQueryAnnotation = hasGraphqlQueryAnnotation(typeClass);
        List<GraphqlMethodMetadata> methods = Stream.of(ReflectionUtils.getAllDeclaredMethods(typeClass))
                .filter(e -> !hasGraphqlIgnoreAnnotation(e))
                .filter(e -> !ReflectionUtils.isObjectMethod(e))
                .filter(e -> hasGraphqlQueryAnnotation || hasGraphqlQueryAnnotation(e))
                .filter(e -> !Modifier.isStatic(e.getModifiers()) && Modifier.isAbstract(e.getModifiers()))
                .map(GraphqlMethodMetadata::of)
                .collect(Collectors.toList());
        return GraphqlMetadata
                .builder()
                .name(graphqlName)
                .typeClass(typeClass)
                .fields(fields)
                .methods(methods)
                .build();
    }

    String name;
    Class<?> typeClass;
    List<GraphqlFieldMetadata> fields;
    List<GraphqlMethodMetadata> methods;

}



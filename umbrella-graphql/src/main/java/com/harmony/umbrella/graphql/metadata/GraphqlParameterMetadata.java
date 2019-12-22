package com.harmony.umbrella.graphql.metadata;

import com.harmony.umbrella.data.Operator;
import com.harmony.umbrella.graphql.annotation.GraphqlParam;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Parameter;
import java.util.Collection;

import static com.harmony.umbrella.graphql.utils.GraphqlUtils.graphqlName;
import static com.harmony.umbrella.graphql.utils.GraphqlUtils.isArrayOrCollection;

@Getter
@ToString
@Builder(access = AccessLevel.PACKAGE)
public class GraphqlParameterMetadata {

    private static Class<?> parameterActualType(Parameter parameter) {
        Class<?> parameterType = parameter.getType();
        if (parameterType.isArray()) {
            return parameterType.getComponentType();
        }
        if (Collection.class.isAssignableFrom(parameterType)) {
            return Object.class;
        }
        return parameterType;
    }

    public static GraphqlParameterMetadata of(Parameter parameter) {
        GraphqlParam ann = AnnotatedElementUtils.getMergedAnnotation(parameter, GraphqlParam.class);
        return GraphqlParameterMetadata
                .builder()
                .name(graphqlName(parameter))
                .parameter(parameter)
                .actualType(parameterActualType(parameter))
                .array(isArrayOrCollection(parameter.getType()))
                .condition(ann == null ? Operator.EQUAL : ann.condition())
                .build();
    }

    String name;
    Parameter parameter;
    Operator condition;
    boolean array;
    Class<?> actualType;

}

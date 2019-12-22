package com.harmony.umbrella.graphql.metadata;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import static com.harmony.umbrella.graphql.utils.GraphqlUtils.graphqlName;
import static com.harmony.umbrella.graphql.utils.GraphqlUtils.isArrayOrCollection;

@Getter
@ToString
@Builder(access = AccessLevel.PACKAGE)
public class GraphqlFieldMetadata {

    private static Class<?> fieldActualType(Field field) {
        Class<?> fieldType = field.getType();
        if (fieldType.isArray()) {
            return fieldType.getComponentType();
        }
        java.lang.reflect.Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
        }
        return fieldType;
    }

    public static GraphqlFieldMetadata of(Field field) {
        return GraphqlFieldMetadata
                .builder()
                .name(graphqlName(field))
                .field(field)
                .actualType(fieldActualType(field))
                .array(isArrayOrCollection(field.getType()))
                .build();
    }

    boolean array;
    String name;
    Field field;
    Class<?> actualType;

}
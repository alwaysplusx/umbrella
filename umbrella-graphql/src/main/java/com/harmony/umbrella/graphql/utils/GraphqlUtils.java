package com.harmony.umbrella.graphql.utils;

import com.harmony.umbrella.graphql.annotation.*;
import com.harmony.umbrella.graphql.metadata.GraphqlFieldMetadata;
import com.harmony.umbrella.graphql.metadata.GraphqlMetadata;
import com.harmony.umbrella.graphql.metadata.GraphqlMethodMetadata;
import com.harmony.umbrella.graphql.metadata.GraphqlParameterMetadata;
import com.harmony.umbrella.graphql.type.FieldType;
import com.harmony.umbrella.graphql.type.MethodType;
import com.harmony.umbrella.graphql.type.ParameterType;
import com.harmony.umbrella.util.StringUtils;
import graphql.Scalars;
import graphql.language.*;
import graphql.schema.GraphQLScalarType;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class GraphqlUtils {

    private static final Map<Class, GraphQLScalarType> PRIMITIVE_TYPES = new HashMap<>();

    static {
        PRIMITIVE_TYPES.put(int.class, Scalars.GraphQLInt);
        PRIMITIVE_TYPES.put(long.class, Scalars.GraphQLBigInteger);
        PRIMITIVE_TYPES.put(float.class, Scalars.GraphQLBigDecimal);
        PRIMITIVE_TYPES.put(double.class, Scalars.GraphQLBigDecimal);
        PRIMITIVE_TYPES.put(char.class, Scalars.GraphQLInt);
        PRIMITIVE_TYPES.put(short.class, Scalars.GraphQLInt);
        PRIMITIVE_TYPES.put(byte.class, Scalars.GraphQLInt);
        PRIMITIVE_TYPES.put(boolean.class, Scalars.GraphQLBoolean);

        PRIMITIVE_TYPES.put(Integer.class, Scalars.GraphQLInt);
        PRIMITIVE_TYPES.put(BigDecimal.class, Scalars.GraphQLBigDecimal);
        PRIMITIVE_TYPES.put(Long.class, Scalars.GraphQLBigInteger);
        PRIMITIVE_TYPES.put(BigInteger.class, Scalars.GraphQLBigInteger);
        PRIMITIVE_TYPES.put(Double.class, Scalars.GraphQLBigDecimal);
        PRIMITIVE_TYPES.put(String.class, Scalars.GraphQLString);
        PRIMITIVE_TYPES.put(Boolean.class, Scalars.GraphQLBoolean);
        PRIMITIVE_TYPES.put(Character.class, Scalars.GraphQLChar);
        PRIMITIVE_TYPES.put(Short.class, Scalars.GraphQLShort);
        PRIMITIVE_TYPES.put(Float.class, Scalars.GraphQLFloat);
        PRIMITIVE_TYPES.put(Byte.class, Scalars.GraphQLByte);
    }

    public static boolean isArrayOrCollection(Class<?> type) {
        return type.isArray() || Collection.class.isAssignableFrom(type);
    }

    public static GraphQLScalarType primitiveType(Class<?> typeClass) {
        return PRIMITIVE_TYPES.get(typeClass);
    }

    public static String graphqlName(AnnotatedElement annotatedElement) {
        if (annotatedElement instanceof Class) {
            return graphqlObjectName((Class<?>) annotatedElement);
        } else if (annotatedElement instanceof Method) {
            return graphqlQueryName((Method) annotatedElement);
        } else if (annotatedElement instanceof Parameter) {
            return graphqlParamName((Parameter) annotatedElement);
        } else if (annotatedElement instanceof Field) {
            return graphqlFieldName((Field) annotatedElement);
        }
        throw new IllegalArgumentException("unsupported type " + annotatedElement);
    }

    public static String graphqlQueryName(Method method) {
        GraphqlQuery ann = AnnotationUtils.getAnnotation(method, GraphqlQuery.class);
        return StringUtils.getFirstNotBlank(ann != null ? ann.name() : null, method.getName());
    }

    public static String graphqlQueryName(Class<?> type) {
        GraphqlQuery ann = AnnotatedElementUtils.getMergedAnnotation(type, GraphqlQuery.class);
        return StringUtils.getFirstNotBlank(ann != null ? ann.name() : null, type.getSimpleName());
    }

    private static String graphqlFieldName(Field field) {
        GraphqlField ann = AnnotatedElementUtils.getMergedAnnotation(field, GraphqlField.class);
        return StringUtils.getFirstNotBlank(ann != null ? ann.name() : null, field.getName());
    }

    private static String graphqlObjectName(Class<?> type) {
        GraphqlObject ann = AnnotatedElementUtils.getMergedAnnotation(type, GraphqlObject.class);
        return StringUtils.getFirstNotBlank(ann != null ? ann.name() : null, type.getSimpleName());
    }

    private static String graphqlParamName(Parameter parameter) {
        GraphqlParam ann = AnnotatedElementUtils.getMergedAnnotation(parameter, GraphqlParam.class);
        return StringUtils.getFirstNotBlank(ann != null ? ann.name() : null, parameter.getName());
    }

    public static boolean hasGraphqlIgnoreAnnotation(AnnotatedElement annotatedElement) {
        return AnnotatedElementUtils.getMergedAnnotation(annotatedElement, GraphqlIgnore.class) != null;
    }

    public static boolean hasGraphqlQueryAnnotation(AnnotatedElement annotatedElement) {
        return AnnotatedElementUtils.getMergedAnnotation(annotatedElement, GraphqlQuery.class) != null;
    }

    public static Class<?> fromClass(Method method) {
        Form ann = AnnotatedElementUtils.getMergedAnnotation(method, Form.class);
        return ann == null
                ? methodActualType(method)
                : ann.value();
    }

    private static Class<?> methodActualType(Method method) {
        Class<?> returnType = method.getReturnType();
        if (returnType.isArray()) {
            return returnType.getComponentType();
        }
        java.lang.reflect.Type genericType = method.getGenericReturnType();
        if (genericType instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
        }
        return returnType;
    }

    public static FieldDefinition fieldDefinition(GraphqlFieldMetadata fieldMetadata) {
        val name = fieldMetadata.getName();
        val field = fieldMetadata.getField();
        val fieldType = new FieldType(fieldMetadata);
        val sourceLocation = fieldSourceLocation(field);
        val description = new Description(name, sourceLocation, false);
        val definition = new FieldDefinition(name, fieldType);
        definition.setDescription(description);
        definition.setSourceLocation(sourceLocation);
        return definition;
    }

    public static FieldDefinition methodDefinition(GraphqlMethodMetadata methodMetadata) {
        val name = methodMetadata.getName();
        val methodType = new MethodType(methodMetadata);
        val sourceLocation = methodSourceLocation(methodMetadata.getMethod());
        val description = new Description(name, sourceLocation, false);
        val definition = new FieldDefinition(name, methodType);
        definition.setSourceLocation(sourceLocation);
        definition.setDescription(description);
        return definition;
    }

    public static ObjectTypeDefinition objectDefinition(GraphqlMetadata objectMetadata) {
        val name = objectMetadata.getName();
        val sourceLocation = typeSourceLocation(objectMetadata.getTypeClass());
        val description = new Description(name, sourceLocation, false);
        List<FieldDefinition> fieldDefinitions =
                objectMetadata
                        .getFields()
                        .stream()
                        .map(GraphqlUtils::fieldDefinition)
                        .collect(Collectors.toList());
        val definition = new ObjectTypeDefinition(name, new ArrayList<>(), new ArrayList<>(), fieldDefinitions);
        definition.setSourceLocation(sourceLocation);
        definition.setDescription(description);
        return definition;
    }

    public static InputValueDefinition parameterDefinition(GraphqlParameterMetadata parameterMetadata) {
        val name = parameterMetadata.getName();
        val parameterType = new ParameterType(parameterMetadata);
        val definition = new InputValueDefinition(name);
        val sourceLocation = parameterSourceLocation(parameterMetadata.getParameter());
        val description = new Description(name, sourceLocation, false);
        definition.setType(parameterType);
        definition.setDescription(description);
        definition.setSourceLocation(sourceLocation);
        return definition;
    }

    private static SourceLocation typeSourceLocation(Class<?> typeClass) {
        return new SourceLocation(-1, -1, typeClass.getName());
    }

    private static SourceLocation methodSourceLocation(Method method) {
        return new SourceLocation(-1, -1, method.toGenericString());
    }

    private static SourceLocation fieldSourceLocation(Field field) {
        return new SourceLocation(-1, -1, field.toGenericString());
    }

    private static SourceLocation parameterSourceLocation(Parameter parameter) {
        return new SourceLocation(-1, -1, parameter.toString());
    }

}

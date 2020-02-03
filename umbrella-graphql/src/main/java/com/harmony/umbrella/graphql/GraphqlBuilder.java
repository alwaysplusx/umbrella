package com.harmony.umbrella.graphql;

import com.harmony.umbrella.graphql.annotation.GraphqlFetcher;
import com.harmony.umbrella.graphql.annotation.GraphqlImport;
import com.harmony.umbrella.graphql.annotation.GraphqlQuery;
import com.harmony.umbrella.graphql.metadata.GraphqlFieldMetadata;
import com.harmony.umbrella.graphql.metadata.GraphqlMetadata;
import com.harmony.umbrella.graphql.metadata.GraphqlMethodMetadata;
import com.harmony.umbrella.graphql.metadata.GraphqlParameterMetadata;
import graphql.schema.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.harmony.umbrella.graphql.utils.GraphqlUtils.*;

@Slf4j
public class GraphqlBuilder implements BeanFactoryAware {

    private static final String GRAPHQL_QUERY_ANNOTATION_NAME = GraphqlQuery.class.getName();

    private DataFetcher<Object> objectDataFetcher;

    private Map<Class<?>, GraphQLObjectType> resolvedTypes = new ConcurrentHashMap<>();

    private List<GraphqlTypeParser> graphqlTypeParsers;

    private BeanFactory beanFactory;

    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    public GraphqlBuilder() {
    }

    public GraphqlBuilder(DataFetcher<Object> objectDataFetcher) {
        this.objectDataFetcher = objectDataFetcher;
    }

    public GraphQLSchema buildSchema(String... basePackages) {
        return buildSchema(loadGraphqlClasses(basePackages));
    }

    public GraphQLSchema buildSchema(Class<?>... classes) {
        return buildSchema(Arrays.asList(classes));
    }

    private GraphQLSchema buildSchema(List<Class<?>> classes) {
        GraphqlQueries graphqlQueries = buildGraphqlQueries(classes);
        return GraphQLSchema
                .newSchema()
                .query(graphqlQueries.toGraphqlQuery())
                .additionalTypes(graphqlQueries.getAdditionalTypes())
                .build();
    }

    private GraphqlQueries buildGraphqlQueries(List<Class<?>> classes) {
        GraphqlQueries graphqlQueries = new GraphqlQueries();
        for (Class<?> graphqlClass : classes) {
            graphqlQueries.append(GraphqlMetadata.of(graphqlClass));
        }
        return graphqlQueries;
    }

    private DataFetcher graphqlDataFetcher(GraphqlMethodMetadata methodMetadata) {
        GraphqlFetcher ann = AnnotatedElementUtils.getMergedAnnotation(methodMetadata.getMethod(), GraphqlFetcher.class);
        return ann == null ? objectDataFetcher : buildGraphqlDataFetcher(ann);
    }

    private DataFetcher buildGraphqlDataFetcher(GraphqlFetcher ann) {
        String beanName = ann.name();
        Class<? extends DataFetcher> fetcherBeanClass = ann.value();
        DataFetcher dataFetcher;
        if (StringUtils.hasText(beanName) && fetcherBeanClass != DataFetcher.class) {
            dataFetcher = beanFactory.getBean(beanName, fetcherBeanClass);
        } else if (StringUtils.hasText(beanName)) {
            dataFetcher = (DataFetcher) beanFactory.getBean(beanName);
        } else if (fetcherBeanClass != DataFetcher.class) {
            dataFetcher = beanFactory.getBean(fetcherBeanClass);
        } else {
            throw new IllegalArgumentException("illegal data fetcher annotation " + ann);
        }
        return dataFetcher;
    }

    private List<Class<?>> loadGraphqlClasses(String... basePackages) {
        List<Class<?>> graphqlQueryClasses = new ArrayList<>();
        for (String basePackage : basePackages) {
            try {
                Resource[] resources = resourcePatternResolver.getResources(resolveBasePackage(basePackage));
                for (Resource resource : resources) {
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
                    ClassMetadata classMetadata = metadataReader.getClassMetadata();
                    if (classMetadata.isInterface()
                            && annotationMetadata.hasAnnotation(GRAPHQL_QUERY_ANNOTATION_NAME)) {
                        try {
                            graphqlQueryClasses.add(Class.forName(classMetadata.getClassName()));
                        } catch (ClassNotFoundException e) {
                            log.warn("graphql query not found. " + classMetadata.getClassName(), e);
                        }
                    }
                }
            } catch (IOException e) {
                log.error("load graphql classes failed", e);
            }
        }
        return graphqlQueryClasses;
    }

    private String resolveBasePackage(String basePackage) {
        return ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + ClassUtils.convertClassNameToResourcePath(basePackage)
                + "/**/*.class";
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    private class GraphqlQueries {

        private Map<String, GraphQLFieldDefinition> queryFieldDefinitions = new HashMap<>();
        private List<Class<?>> graphqlClasses = new ArrayList<>();
        private Set<Class<?>> additionalTypes = new HashSet<>();
        private Map<Class<?>, GraphQLObjectType> resolvedAdditionalTypes = new HashMap<>();

        public GraphQLObjectType toGraphqlQuery() {
            return GraphQLObjectType
                    .newObject()
                    .name("Query")
                    .description("graphql of classes " + graphqlClasses)
                    .fields(new ArrayList<>(queryFieldDefinitions.values()))
                    .build();
        }

        public Set<GraphQLType> getAdditionalTypes() {
            return new HashSet<>(resolvedAdditionalTypes.values());
        }

        public void append(GraphqlMetadata metadata) {
            Class<?> typeClass = metadata.getTypeClass();
            graphqlClasses.add(typeClass);
            GraphqlImport ann = AnnotationUtils.getAnnotation(typeClass, GraphqlImport.class);
            if (ann != null) {
                Collections.addAll(additionalTypes, ann.classes());
            }
            for (GraphqlMethodMetadata method : metadata.getMethods()) {
                String queryName = method.getName();
                if (queryFieldDefinitions.containsKey(queryName)) {
                    throw new IllegalArgumentException(queryName + "query name already exists");
                }
                queryFieldDefinitions.put(queryName, buildQueryFieldDefinition(method));
            }
            resolveAdditionalTypes();
        }

        private void resolveAdditionalTypes() {
            if (!additionalTypes.isEmpty()) {
                List<Class<?>> types = new ArrayList<>(additionalTypes);
                additionalTypes.clear();
                for (Class<?> type : types) {
                    GraphQLObjectType resolvedType = resolvedTypes.get(type);
                    if (resolvedType == null) {
                        resolvedType = buildGraphqlObject(type);
                    }
                    resolvedAdditionalTypes.put(type, resolvedType);
                }
            }
        }

        private GraphQLFieldDefinition buildQueryFieldDefinition(GraphqlMethodMetadata methodMetadata) {
            String graphqlQueryName = methodMetadata.getName();
            return GraphQLFieldDefinition
                    .newFieldDefinition()
                    .name(graphqlQueryName)
                    .type(buildGraphqlObject(methodMetadata.getMethod().getReturnType()))
                    .argument(
                            methodMetadata
                                    .getParameters()
                                    .stream()
                                    .map(this::buildGraphqlQueryArgument)
                                    .collect(Collectors.toList())
                    )
                    .dataFetcher(graphqlDataFetcher(methodMetadata))
                    .definition(methodDefinition(methodMetadata))
                    .build();
        }

        private GraphQLObjectType buildGraphqlObject(Class<?> typeClass) {
            GraphqlMetadata metadata = GraphqlMetadata.of(typeClass);
            GraphQLObjectType objectType = GraphQLObjectType
                    .newObject()
                    .name(metadata.getName())
                    .description(typeClass.getSimpleName())
                    .definition(objectDefinition(metadata))
                    .fields(
                            metadata
                                    .getFields()
                                    .stream()
                                    .map(this::buildGraphqlObjectField)
                                    .collect(Collectors.toList())
                    )
                    .build();
            resolvedTypes.put(typeClass, objectType);
            return objectType;
        }

        private GraphQLFieldDefinition buildGraphqlObjectField(GraphqlFieldMetadata fieldMetadata) {
            String name = fieldMetadata.getName();
            return GraphQLFieldDefinition
                    .newFieldDefinition()
                    .name(name)
                    .type(buildFieldType(fieldMetadata))
                    .definition(fieldDefinition(fieldMetadata))
                    .description(name)
                    .build();
        }

        private GraphQLArgument buildGraphqlQueryArgument(GraphqlParameterMetadata parameterMetadata) {
            return GraphQLArgument
                    .newArgument()
                    .name(parameterMetadata.getName())
                    .type(buildArgumentType(parameterMetadata))
                    .definition(parameterDefinition(parameterMetadata))
                    .build();
        }

        private GraphQLOutputType buildFieldType(GraphqlFieldMetadata fieldMetadata) {
            Class<?> actualType = fieldMetadata.getActualType();
            GraphQLOutputType fieldObjectType = primitiveType(actualType);
            if (fieldObjectType == null) {
                additionalTypes.add(actualType);
                fieldObjectType = GraphQLTypeReference.typeRef(graphqlName(actualType));
            }
            return fieldMetadata.isArray() ? GraphQLList.list(fieldObjectType) : fieldObjectType;
        }

        private GraphQLInputType buildArgumentType(GraphqlParameterMetadata parameterMetadata) {
            Class<?> actualType = parameterMetadata.getActualType();
            GraphQLInputType type = primitiveType(actualType);
            if (type == null) {
                additionalTypes.add(actualType);
                type = GraphQLTypeReference.typeRef(graphqlName(actualType));
            }
            return parameterMetadata.isArray() ? GraphQLList.list(type) : type;
        }

    }

}

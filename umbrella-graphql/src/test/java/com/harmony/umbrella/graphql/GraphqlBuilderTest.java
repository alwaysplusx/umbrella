package com.harmony.umbrella.graphql;

import com.harmony.umbrella.graphql.query.AuthorGraphql;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class GraphqlBuilderTest {

    @Test
    public void testBuild() {
        GraphqlBuilder builder = new GraphqlBuilder();
        GraphQLSchema schema = builder.buildSchema("com.harmony.umbrella.graphql.*");
        GraphQL graphQL = GraphQL.newGraphQL(schema).build();
        String query = "query { author(id: \"1\") { name title } }";
        ExecutionResult result = graphQL.execute(query);
        log.info("query: {} with schema: {}, query result: {}", query, schema, result);
    }

    @Test
    public void testSignalBuild() {
        GraphqlBuilder builder = new GraphqlBuilder(new JpaDataFetcher());
        GraphQLSchema schema = builder.buildSchema(AuthorGraphql.class);
        GraphQL graphQL = GraphQL.newGraphQL(schema).build();
        ExecutionResult result = graphQL.execute("query { author(id: \"1\") { name title } }");
        log.info("query author, result: {}", result);
    }

}

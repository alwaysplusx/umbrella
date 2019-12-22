package com.harmony.umbrella.graphql.query;

import com.harmony.umbrella.graphql.annotation.GraphqlQuery;
import com.harmony.umbrella.graphql.annotation.Like;
import com.harmony.umbrella.graphql.model.Author;

@GraphqlQuery
public interface AuthorGraphql {

    Author author(@Like("id") String id);

}

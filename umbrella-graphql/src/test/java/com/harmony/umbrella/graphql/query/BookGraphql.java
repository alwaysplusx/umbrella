package com.harmony.umbrella.graphql.query;

import com.harmony.umbrella.graphql.query.annotation.Equal;
import com.harmony.umbrella.graphql.query.annotation.Form;
import com.harmony.umbrella.graphql.annotation.GraphqlQuery;
import com.harmony.umbrella.graphql.model.Book;

@GraphqlQuery
@Form(Book.class)
public interface BookGraphql {

    Book book(@Equal("id") String id);

}

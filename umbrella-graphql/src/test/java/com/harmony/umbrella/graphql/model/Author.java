package com.harmony.umbrella.graphql.model;

import com.harmony.umbrella.graphql.annotation.GraphqlObject;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@GraphqlObject
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Author {

    String name;
    String title;
    List<Book> books;
}

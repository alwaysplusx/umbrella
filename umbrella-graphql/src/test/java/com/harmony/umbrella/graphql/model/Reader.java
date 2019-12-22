package com.harmony.umbrella.graphql.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Reader {

    String name;
    List<Book> favoriteBooks;
    List<Author> favoriteAuthors;

}

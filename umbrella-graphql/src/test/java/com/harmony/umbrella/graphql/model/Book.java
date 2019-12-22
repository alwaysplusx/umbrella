package com.harmony.umbrella.graphql.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Book {

    String bookId;
    String bookName;
    Author author;

}

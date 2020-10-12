package com.harmony.umbrella.query.jpa.factory;

import com.harmony.umbrella.query.jpa.JpaSortBuilder;

public interface JpaSortBuilderFactory {

    <T> JpaSortBuilder<T> newSortBuilder();

}

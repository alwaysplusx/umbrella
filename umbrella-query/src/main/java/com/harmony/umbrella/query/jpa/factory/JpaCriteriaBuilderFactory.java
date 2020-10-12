package com.harmony.umbrella.query.jpa.factory;

import com.harmony.umbrella.query.jpa.JpaCriteriaBuilder;

public interface JpaCriteriaBuilderFactory {

    <T> JpaCriteriaBuilder<T> newCriteriaBuilder(Class<T> domainClass);

}

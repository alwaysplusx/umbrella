package com.harmony.umbrella.query.jpa;

public interface JpaCriteriaBuilderFactory {

    <T> JpaCriteriaBuilder<T> newCriteriaBuilder(Class<T> domainClass);

}

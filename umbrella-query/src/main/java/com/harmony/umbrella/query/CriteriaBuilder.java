package com.harmony.umbrella.query;

import java.util.Collection;

public interface CriteriaBuilder<DOMAIN, BUILDER extends CriteriaBuilder<DOMAIN, BUILDER>> {

    <T> BUILDER equal(Path<DOMAIN> column, Object value);

    <T> BUILDER equal(PathFunction<DOMAIN, T> column, T value);

    <T> BUILDER notEqual(Path<DOMAIN> column, Object value);

    <T> BUILDER notEqual(PathFunction<DOMAIN, T> column, T value);

    <T> BUILDER in(PathFunction<DOMAIN, T> column, T... values);

    <T> BUILDER in(PathFunction<DOMAIN, T> column, Collection<T> values);

    <T> BUILDER notIn(PathFunction<DOMAIN, T> column, T... values);

    <T> BUILDER notIn(PathFunction<DOMAIN, T> column, Collection<T> values);

//
//    <T> BUILDER lessThan(PathFunction<DOMAIN, T> column, T value);
//
//    <T> BUILDER lessThanOrEquals(PathFunction<DOMAIN, T> column, T value);
//
//    <T> BUILDER greaterThan(PathFunction<DOMAIN, T> column, T value);
//
//    <T> BUILDER greaterThanOrEquals(PathFunction<DOMAIN, T> column, T value);
//
//    <T> BUILDER like(PathFunction<DOMAIN, T> column, T value);
//
//    <T> BUILDER notLike(PathFunction<DOMAIN, T> column, T value);
//
//    BUILDER isNull(PathFunction<DOMAIN, ?> column);
//
//    BUILDER isNotNull(PathFunction<DOMAIN, ?> column);
//
//    BUILDER isTrue(PathFunction<DOMAIN, ?> column);
//
//    BUILDER isFalse(PathFunction<DOMAIN, ?> column);

}

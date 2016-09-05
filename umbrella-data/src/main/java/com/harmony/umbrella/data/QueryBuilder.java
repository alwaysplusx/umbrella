package com.harmony.umbrella.data;

import java.util.Collection;

/**
 * @author wuxii@foxmail.com
 */
public interface QueryBuilder<T extends QueryBuilder<T>> {

    T equal(String name, Object value);

    T notEqual(String name, Object value);

    T like(String name, Object value);

    T notLike(String name, Object value);

    T in(String name, Object value);

    T in(String name, Collection<?> value);

    T in(String name, Object... value);

    T notIn(String name, Object value);

    T notIn(String name, Collection<?> value);

    T notIn(String name, Object... value);

    T between(String name, Object left, Object right);

    T notBetween(String name, Object left, Object right);

    T greatThen(String name, Object value);

    T greatEqual(String name, Object value);

    T lessThen(String name, Object value);

    T lessEqual(String name, Object value);

    T isNull(String name);

    T notNull(String name);

    T and();

    T or();

}

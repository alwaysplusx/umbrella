package com.harmony.umbrella.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

/**
 * @author wuxii@foxmail.com
 */
public class QueryBuilder<T extends QueryBuilder<T>> {

    private Stack<Bind> queryStack = new Stack<Bind>();

    private List<Bind> queryBind = new ArrayList<Bind>();

    public T equal(String name, Object value) {
        return (T) this;
    }

    public T notEqual(String name, Object value) {
        return (T) this;
    }

    public T like(String name, Object value) {
        return (T) this;
    }

    public T notLike(String name, Object value) {
        return (T) this;
    }

    public T in(String name, Object value) {
        return (T) this;
    }

    public T in(String name, Collection<?> value) {
        return (T) this;
    }

    public T in(String name, Object... value) {
        return (T) this;
    }

    public T notIn(String name, Object value) {
        return (T) this;
    }

    public T notIn(String name, Collection<?> value) {
        return (T) this;
    }

    public T notIn(String name, Object... value) {
        return (T) this;
    }

    public T between(String name, Object left, Object right) {
        return (T) this;
    }

    public T notBetween(String name, Object left, Object right) {
        return (T) this;
    }

    public T greatThen(String name, Object value) {
        return (T) this;
    }

    public T greatEqual(String name, Object value) {
        return (T) this;
    }

    public T lessThen(String name, Object value) {
        return (T) this;
    }

    public T lessEqual(String name, Object value) {
        return (T) this;
    }

    public T isNull(String name) {
        return (T) this;
    }

    public T notNull(String name) {
        return (T) this;
    }

    public T buildCondition(String name, Object value, Operator operator) {
        Bind bind = queryStack.peek();
        bind.add(new BindItem(name, value, operator));
        return (T) this;
    }

    private Bind peek() {
        if (queryStack.isEmpty()) {
            start();
        }
        return queryStack.peek();
    }

    public T start() {
        queryStack.push(new Bind());
        return (T) this;
    }

    public T end() {
        queryBind.add(queryStack.pop());
        return (T) this;
    }

    public T and() {
        return (T) this;
    }

    public T or() {
        return (T) this;
    }

}

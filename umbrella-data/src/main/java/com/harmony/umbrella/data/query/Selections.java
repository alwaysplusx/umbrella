package com.harmony.umbrella.data.query;

import com.harmony.umbrella.data.query.select.ColumnBuilder;
import com.harmony.umbrella.data.query.select.CountBuilder;
import com.harmony.umbrella.data.query.select.SelectionBuilder;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wuxii
 */
public class Selections {

    public static Selections countRoot() {
        return new Selections().count();
    }

    private List<SelectionBuilder> builders = new ArrayList<>();

    public Selections root() {
        return this;
    }

    public Selections count() {
        return null;
    }

    public Selections columns(String... names) {
        Stream.of(names).forEach(this::column);
        return this;
    }

    public CountBuilder count(String name) {
        return apply(new CountBuilder(name));
    }

    public ColumnBuilder column(String name) {
        return apply(new ColumnBuilder(name));
    }

    public ColumnBuilder column() {
        return apply(new ColumnBuilder());
    }

    public <X extends SelectionBuilder> X apply(X builder) {
        builders.add(builder);
        return builder;
    }

    protected <X> List<Column> generate(Root<X> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return builders.stream().map(e -> e.generate(root, query, cb)).collect(Collectors.toList());
    }

}

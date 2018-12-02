package com.harmony.umbrella.data;

import com.harmony.umbrella.data.model.QueryModel;
import com.harmony.umbrella.data.select.ColumnSelection;
import com.harmony.umbrella.data.select.CountSelection;
import com.harmony.umbrella.data.select.RootCountSelection;
import com.harmony.umbrella.data.select.RootSelection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wuxii
 */
public class Selections {

    // static builder

    /**
     * 查询字段
     *
     * @param names 待查的字段
     * @return
     */
    public static Selections of(String... names) {
        return new Selections().column(names);
    }

    //

    private final List<Selection> selections = new ArrayList<>();

    public Selections root() {
        return apply(new RootSelection(this)).and();
    }

    public Selections column(String name) {
        return columnSelection().setName(name).and();
    }

    public Selections column(String... name) {
        Stream.of(name).forEach(this::column);
        return this;
    }

    public Selections countRoot(boolean distinct) {
        return apply(new RootCountSelection(this)).setDistinct(distinct).and();
    }

    public Selections count(String name) {
        return countSelection().setName(name).and();
    }

    public Selections countDistinct(String name) {
        return countSelection().setDistinct(true).setName(name).and();
    }

    // other aggregate functions

    public Selections sum(String name) {
        return function("sum", name);
    }

    public Selections avg(String name) {
        return function("avg", name);
    }

    public Selections max(String name) {
        return function("max", name);
    }

    public Selections min(String name) {
        return function("min", name);
    }

    public Selections abs(String name) {
        return function("abs", name);
    }

    public Selections sqrt(String name) {
        return function("sqrt", name);
    }

    protected Selections function(String function, String name) {
        return apply(new FunctionSelection(this)).setFunctionName(function).setName(name).and();
    }

    // builder, 用于构建复杂的字段

    public ColumnSelection columnSelection() {
        return apply(new ColumnSelection(this));
    }

    public CountSelection countSelection() {
        return apply(new CountSelection(this));
    }

    public <X extends SelectionBuilder<X>> X apply(X builder) {
        selections.add(builder);
        return builder;
    }

    // final generate

    public List<Column> generate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return selections.stream().map(e -> e.generate(root, query, cb)).collect(Collectors.toList());
    }

    protected static class FunctionSelection<X> extends SelectionBuilder<FunctionSelection<X>> {

        private String functionName;
        private String name;

        protected FunctionSelection(Selections selections) {
            super(selections);
        }

        @Override
        protected Expression buildSelection(QueryModel queryModel) {
            Expression exp = queryModel.get(name);
            CriteriaBuilder cb = queryModel.getCriteriaBuilder();
            switch (functionName) {
                case "count":
                    return cb.count(exp);
                case "avg":
                    return cb.avg(exp);
                case "max":
                    return cb.max(exp);
                case "min":
                    return cb.min(exp);
                case "sum":
                    return cb.sum(exp);
                case "abs":
                    return cb.abs(exp);
                case "sqrt":
                    return cb.sqrt(exp);
            }
            throw new QueryException("unknow function " + name);
        }

        public FunctionSelection<X> setFunctionName(String functionName) {
            this.functionName = functionName;
            return this;
        }

        public FunctionSelection<X> setName(String name) {
            this.name = name;
            return this;
        }

    }

}

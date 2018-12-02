package com.harmony.umbrella.data.result;

import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wuxii
 */
public class ResultList implements Iterable<RowResult> {

    private final List<RowResult> rows;

    public ResultList(List<RowResult> rows) {
        Assert.notNull(rows, "result list is not allow null");
        this.rows = rows;
    }

    public <T> Optional mapFirst(Class<T> resultClass) {
        return mapFirst(ResultConverter.convertFor(resultClass));
    }

    public <T> Optional mapFirst(Function<RowResult, T> fun) {
        return rows.stream().findFirst().map(fun);
    }

    public int size() {
        return rows.size();
    }

    public <T> List<T> mapRows(Class<T> resultClass) {
        return mapRows(ResultConverter.convertFor(resultClass));
    }

    public <T> List<T> mapRows(Function<RowResult, T> fun) {
        return rows.stream().map(fun).collect(Collectors.toList());
    }

    @Override
    public Iterator<RowResult> iterator() {
        return rows.iterator();
    }

    public Stream<RowResult> stream() {
        return rows.stream();
    }

}

package com.harmony.umbrella.data.result;

import com.harmony.umbrella.data.Column;
import org.springframework.data.util.Streamable;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wuxii
 */
public class ListResult implements Streamable<RowResult> {

    private final List<RowResult> rows;

    ListResult(List<Column> columns, List<Object> values) {
        Assert.isTrue(columns != null && !columns.isEmpty(), "none select");
        this.rows = values.stream().map(v -> new RowResult(columns, v)).collect(Collectors.toList());
    }

    ListResult(List<RowResult> rowList) {
        this.rows = rowList;
    }

    public int size() {
        return rows.size();
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }

    public <T> List<T> toList(Class<T> resultClass) {
        Function<RowResult, T> converter = RowResult.defaultRowResultConverter(resultClass);
        return this.stream().map(converter).collect(Collectors.toList());
    }

    @Override
    public Iterator<RowResult> iterator() {
        return rows.iterator();
    }

}

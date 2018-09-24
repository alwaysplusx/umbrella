package com.harmony.umbrella.data.query;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author wuxii
 */
public class Result implements Iterable<RowResult> {

    private static final Result EMPTY = new Result(Collections.emptyList());

    public static Result empty() {
        return EMPTY;
    }

    final List<RowResult> rowResults;

    Result() {
        this(new ArrayList<>());
    }

    Result(List<RowResult> results) {
        this.rowResults = results;
    }

    public <T> Optional<T> convert(Class<T> resultClass) {
        return convert(ResultConverter.convertFor(resultClass));
    }

    public <T> Optional<T> convert(Function<Result, T> converter) {
        return rowResults.isEmpty()
                ? Optional.empty()
                : Optional.of(converter.apply(this));
    }

    @Override
    public Iterator<RowResult> iterator() {
        return rowResults.iterator();
    }

    public Stream<RowResult> stream() {
        return null;
    }

    Result add(RowResult rowResult) {
        this.rowResults.add(rowResult);
        return this;
    }

}

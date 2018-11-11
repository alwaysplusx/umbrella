package com.harmony.umbrella.data.query;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 查询的行记录
 *
 * @author wuxii
 */
public class Result implements Iterable<CellResult> {

    public static CellResult firstCellResult(Result result) {
        return result.stream().findFirst().orElse(null);
    }

    private static final Result EMPTY = new Result(Collections.emptyList());

    public static Result empty() {
        return EMPTY;
    }

    final List<CellResult> cellResults;

    Result() {
        this(new ArrayList<>());
    }

    Result(List<CellResult> results) {
        this.cellResults = results;
    }

    public <T> Optional<T> convert(Class<T> resultClass) {
        return convert(ResultConverter.convertFor(resultClass));
    }

    public <T> Optional<T> convert(Function<Result, T> converter) {
        return cellResults.isEmpty()
                ? Optional.empty()
                : Optional.of(converter.apply(this));
    }

    @Override
    public Iterator<CellResult> iterator() {
        return cellResults.iterator();
    }

    public Stream<CellResult> stream() {
        return cellResults.stream();
    }

    Result add(CellResult cellResult) {
        this.cellResults.add(cellResult);
        return this;
    }

}

package com.harmony.umbrella.data.result;

import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author wuxii
 */
public class RowResult implements Iterable<CellResult> {

    private static final RowResult EMPTY = new RowResult(Collections.emptyList());

    public static RowResult empty() {
        return EMPTY;
    }

    public static CellResult firstCellResult(RowResult row) {
        return row.cellResults.isEmpty() ? null : row.get(0);
    }

    private List<CellResult> cellResults;

    public RowResult(List<CellResult> cellResults) {
        Assert.notNull(cellResults, "cell result not allow null");
        this.cellResults = Collections.unmodifiableList(cellResults);
    }

    @Override
    public Iterator<CellResult> iterator() {
        return cellResults.iterator();
    }

    public Stream<CellResult> stream() {
        return cellResults.stream();
    }

    public CellResult get(int index) {
        return cellResults.get(index);
    }

    public boolean isPresent() {
        return !cellResults.isEmpty();
    }

    public <T> Optional<T> mapTo(Class<T> resultClass) {
        return map(ResultConverter.convertFor(resultClass));
    }

    public <T> Optional<T> map(Function<RowResult, T> fun) {
        return Optional.ofNullable(fun.apply(this));
    }

}

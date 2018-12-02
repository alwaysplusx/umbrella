package com.harmony.umbrella.data.result;

import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author wuxii
 */
public class RowResult implements Iterable<CellResult> {

    private static final RowResult EMPTY = new RowResult(Collections.emptyList());

    public static RowResult empty() {
        return EMPTY;
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

}

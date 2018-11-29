package com.harmony.umbrella.data.query;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author wuxii
 */
public class RowResult implements Iterable<CellResult> {

    private List<CellResult> cellResults;

    public RowResult(List<CellResult> cellResults) {
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

}

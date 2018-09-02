package com.harmony.umbrella.data.util;

import com.harmony.umbrella.data.result.ColumnResult;
import com.harmony.umbrella.data.result.SelectionAndResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

class SelectionAndResultImpl implements SelectionAndResult {

    private List<ColumnResult> columnResults;

    public SelectionAndResultImpl(ColumnResult columnResult) {
        this.columnResults = Arrays.asList(columnResult);
    }

    public SelectionAndResultImpl(List<ColumnResult> columnResults) {
        this.columnResults = Collections.unmodifiableList(columnResults);
    }

    @Override
    public ColumnResult get(int index) {
        return columnResults.get(index);
    }

    @Override
    public ColumnResult get(String name) {
        return columnResults
                .stream()
                .filter(e -> e.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Iterator<ColumnResult> iterator() {
        return columnResults.iterator();
    }

}
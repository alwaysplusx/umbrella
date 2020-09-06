package com.harmony.umbrella.data.result;

import com.harmony.umbrella.data.Column;
import org.springframework.data.util.Streamable;
import org.springframework.util.Assert;

import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.util.*;
import java.util.function.Function;

/**
 * @author wuxii
 */
public class RowResult implements Streamable<CellValue> {

    private static final RowResult EMPTY = new RowResult();

    public static RowResult empty() {
        return EMPTY;
    }

    public static CellValue firstCellResult(RowResult row) {
        return row.values.isEmpty() ? null : row.get(0);
    }

    private static Object[] adaptedValues(Object value) {
        return value.getClass().isArray() ? (Object[]) value : new Object[]{value};
    }

    private final List<Column> columns;
    private final List<Object> values;

    private RowResult() {
        this.columns = Collections.emptyList();
        this.values = Collections.emptyList();
    }

    RowResult(List<Column> columns, Object value) {
        this(columns, adaptedValues(value));
    }

    RowResult(List<Column> columns, Object... values) {
        Assert.notEmpty(columns, "none select");
        this.columns = Collections.unmodifiableList(columns);
        this.values = Collections.unmodifiableList(Arrays.asList(values));
    }

    public CellValue get(int index) {
        return new CellValue(index, columns.get(index), values.get(index));
    }

    public <T> T toEntity(Function<RowResult, T> converter) {
        return hasCellResult() ? converter.apply(this) : null;
    }

    public <T> Optional<T> mapToEntity(Function<RowResult, T> rowConverter) {
        return Optional.ofNullable(toEntity(rowConverter));
    }

    public boolean hasCellResult() {
        return !values.isEmpty();
    }

    @Override
    public Iterator<CellValue> iterator() {
        return new CellIterator();
    }

    private boolean isRootResult(Class<?> rootType) {
        if (columns.size() != 1) {
            return false;
        }
        Selection<?> selection = columns.get(0).getExpression();
        return selection instanceof Root && selection.getJavaType() == rootType;
    }

    class CellIterator implements Iterator<CellValue> {

        int index = 0;

        @Override
        public boolean hasNext() {
            return index < values.size();
        }

        @Override
        public CellValue next() {
            int index = this.index++;
            return new CellValue(index, columns.get(index), values.get(index));
        }

    }

}

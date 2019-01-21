package com.harmony.umbrella.data.result;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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

    public <T> T toEntity(Class<T> resultClass) {
        return toEntity(new RowResultMapper<>(resultClass));
    }

    public <T> T toEntity(Function<RowResult, T> fun) {
        return fun.apply(this);
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
            return index == values.size();
        }

        @Override
        public CellValue next() {
            int index = this.index++;
            return new CellValue(index, columns.get(index), values.get(index));
        }

    }

    static class RowResultMapper<T> implements Function<RowResult, T> {

        private final Class<T> resultClass;

        private RowResultMapper(Class<T> resultClass) {
            this.resultClass = resultClass;
        }

        @Override
        public T apply(RowResult row) {
            if (!row.hasCellResult()) {
                return null;
            }
            if (row.isRootResult(resultClass)) {
                return (T) firstCellResult(row).getValue();
            }
            Map<String, Object> map = new HashMap<>();
            for (CellValue cell : row) {
                String name = cell.getName();
                applyValue(map, name, cell.getValue());
            }
            return JSON.toJavaObject(new JSONObject(map), resultClass);
        }

        private void applyValue(Map<String, Object> map, String path, Object value) {
            String[] names = path.split("\\.");
            Map<String, Object> current = map;
            for (int i = 0; i < names.length - 1; i++) {
                String name = names[i];
                Map<String, Object> temp = (Map<String, Object>) current.get(name);
                if (temp == null) {
                    temp = new HashMap<>();
                    current.put(name, temp);
                }
                current = temp;
            }
            current.put(names[names.length - 1], value);
        }


    }

}

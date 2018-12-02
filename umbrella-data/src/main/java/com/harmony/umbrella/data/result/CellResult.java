package com.harmony.umbrella.data.result;

import com.harmony.umbrella.data.Column;

/**
 * 行记录中的列值
 *
 * @author wuxii
 */
public class CellResult {

    public static String stringValue(CellResult cellResult) {
        return cellResult.value == null ? null : cellResult.value.toString();
    }

    private final int index;
    private final Column column;
    private final Object value;

    public CellResult(int index, Column column, Object value) {
        this.index = index;
        this.column = column;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return column.getAlias();
    }

    public Column getColumn() {
        return column;
    }

    public Object getValue() {
        return value;
    }

    public Class<?> getJavaType() {
        return value == null ? null : value.getClass();
    }

}
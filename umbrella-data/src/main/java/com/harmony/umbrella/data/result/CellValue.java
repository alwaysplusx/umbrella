package com.harmony.umbrella.data.result;

import com.harmony.umbrella.data.Column;

import javax.persistence.criteria.Selection;

/**
 * 行记录中的列值
 *
 * @author wuxii
 */
public class CellValue {

    public static String stringValue(CellValue cellValue) {
        return cellValue.value == null ? null : cellValue.value.toString();
    }

    private final int index;
    private final Object value;
    private final Column column;

    public CellValue(int index, Column column, Object value) {
        this.index = index;
        this.value = value;
        this.column = column;
    }

    public String getName() {
        return column.getAlias();
    }

    public Selection<?> getSelection() {
        return column.getExpression();
    }

    public Column getColumn() {
        return column;
    }

    public int getIndex() {
        return index;
    }

    public Object getValue() {
        return value;
    }

    public Class<?> getJavaType() {
        return value == null ? null : value.getClass();
    }

}
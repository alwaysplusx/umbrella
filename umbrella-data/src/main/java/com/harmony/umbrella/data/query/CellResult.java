package com.harmony.umbrella.data.query;

import com.harmony.umbrella.data.model.ExpressionModel;

import javax.persistence.criteria.Selection;

/**
 * 行记录中的列值
 *
 * @author wuxii
 */
public class CellResult {

    public static String stringValue(CellResult cellResult) {
        return cellResult.value == null ? null : cellResult.value.toString();
    }

    private int index;
    private Selection<?> selection;
    private Object value;

    public CellResult(int index, Selection<?> selection, Object value) {
        this.index = index;
        this.selection = selection;
        this.value = value;
    }

    public Selection<?> getSelection() {
        return selection;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return selection instanceof ExpressionModel
                ? ((ExpressionModel) selection).getPath()
                : selection.getAlias();
    }

    public Object getValue() {
        return value;
    }

    public Class<?> getJavaType() {
        return value == null ? null : value.getClass();
    }

}
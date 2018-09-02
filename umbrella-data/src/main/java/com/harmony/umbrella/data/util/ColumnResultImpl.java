package com.harmony.umbrella.data.util;

import com.harmony.umbrella.data.model.ExpressionModel;
import com.harmony.umbrella.data.result.ColumnResult;

import javax.persistence.criteria.Selection;

class ColumnResultImpl implements ColumnResult {

    private int index;
    private Selection<?> selection;
    private Object value;

    public ColumnResultImpl(int index, Selection<?> selection, Object value) {
        this.index = index;
        this.selection = selection;
        this.value = value;
    }

    @Override
    public Selection<?> getSelection() {
        return selection;
    }

    @Override
    public Object getResult() {
        return value;
    }

    @Override
    public Class<?> getJavaType() {
        return value == null ? null : value.getClass();
    }

    @Override
    public String getName() {
        return selection instanceof ExpressionModel
                ? ((ExpressionModel) selection).getPath()
                : selection.getAlias();
    }

    @Override
    public int getIndex() {
        return index;
    }
}
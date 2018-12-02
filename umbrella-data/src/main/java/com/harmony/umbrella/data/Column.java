package com.harmony.umbrella.data;

import javax.persistence.criteria.Selection;

/**
 * @author wuxii
 */
public class Column {

    private String alias;
    private Selection<?> selection;

    public Column(String alias, Selection<?> selection) {
        this.alias = alias;
        this.selection = selection;
    }

    public Selection<?> getSelection() {
        return selection;
    }

    public String getAlias() {
        return alias;
    }

}

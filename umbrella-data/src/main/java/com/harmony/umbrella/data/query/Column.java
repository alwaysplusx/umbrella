package com.harmony.umbrella.data.query;

import javax.persistence.criteria.Selection;

/**
 * @author wuxii
 */
public class Column {

    private String name;
    private String alias;
    private Selection selection;

    public Column(String name, String alias, Selection<?> selection) {
        this.name = name;
        this.alias = alias;
        this.selection = selection;
    }

    public String getName() {
        return name;
    }

    public Selection<?> getSelection() {
        return selection;
    }

    public String getAlias() {
        return alias;
    }

}

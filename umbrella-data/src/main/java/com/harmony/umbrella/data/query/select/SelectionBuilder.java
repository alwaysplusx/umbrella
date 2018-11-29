package com.harmony.umbrella.data.query.select;

/**
 * @param <T> builder
 * @author wuxii
 */
public abstract class SelectionBuilder<X, T extends SelectionBuilder> implements SelectionGenerator<X> {

    protected String alias;

    public T alias(String alias) {
        this.alias = alias;
        return (T) this;
    }

}

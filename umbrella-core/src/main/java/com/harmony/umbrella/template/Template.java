package com.harmony.umbrella.template;

/**
 * @author wuxii
 */
public interface Template<T extends Expression> {

    T getExpression();

    Object getValue(Object rootObject);

}

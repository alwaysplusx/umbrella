package com.harmony.umbrella.template;

/**
 * @author wuxii
 */
public interface ExpressionTemplate<T extends Expression> extends Iterable<T> {

    String getTemplate();

    String getValue(Object rootObject);

}

package com.harmony.umbrella.template.support;

import com.harmony.umbrella.template.Expression;
import com.harmony.umbrella.template.Template;

/**
 * @author wuxii
 */
public class SimpleTextTemplate<T extends Expression> implements Template<T> {

    private T expression;

    public SimpleTextTemplate(T expression) {
        this.expression = expression;
    }

    @Override
    public T getExpression() {
        return expression;
    }

    @Override
    public String getValue(Object rootObject) {
        return expression.getText();
    }

    @Override
    public String toString() {
        return "Template{" +
                "expression=" + expression +
                '}';
    }
}

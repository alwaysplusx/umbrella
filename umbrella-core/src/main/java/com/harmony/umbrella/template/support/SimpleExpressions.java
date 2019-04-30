package com.harmony.umbrella.template.support;

import com.harmony.umbrella.template.Expression;
import com.harmony.umbrella.template.Expressions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author wuxii
 */
public class SimpleExpressions implements Expressions {

    private String text;
    private List<Expression> expressions = new ArrayList<>();

    public SimpleExpressions() {
    }

    public SimpleExpressions(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Iterator<Expression> iterator() {
        return expressions.iterator();
    }

    public void setText(String text) {
        this.text = text;
    }

    public <T extends Expression> void setExpressions(List<T> expressions) {
        this.expressions = new ArrayList<>(expressions);
    }

    public <T extends Expression> void addExpression(T expression) {
        this.expressions.add(expression);
    }

    @Override
    public String toString() {
        return text;
    }

}

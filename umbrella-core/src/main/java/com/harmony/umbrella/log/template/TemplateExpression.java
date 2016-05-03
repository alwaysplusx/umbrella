package com.harmony.umbrella.log.template;

import com.harmony.umbrella.el.Expression;


/**
 * @author wuxii@foxmail.com
 */
public class TemplateExpression extends Expression {

    private boolean isText;
    private int index;

    public TemplateExpression(String expressionText, String delimiter, boolean isText, int index) {
        super(expressionText, delimiter);
        this.isText = isText;
        this.index = index;
    }

    public boolean isText() {
        return isText;
    }

    public int getIndex() {
        return index;
    }

}

package com.harmony.umbrella.template;

/**
 * @author wuxii
 */
public class SimpleTextTemplateItem implements TemplateItem {

    private Expression expression;

    public SimpleTextTemplateItem(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public String getValue(Object rootObject) {
        return expression.getText();
    }

}

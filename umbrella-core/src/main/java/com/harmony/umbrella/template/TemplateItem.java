package com.harmony.umbrella.template;

/**
 * @author wuxii
 */
public interface TemplateItem {

    Expression getExpression();

    Object getValue(Object rootObject);

}

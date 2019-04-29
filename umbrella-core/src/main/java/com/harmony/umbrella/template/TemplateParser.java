package com.harmony.umbrella.template;

/**
 * @author wuxii
 */
public interface TemplateParser {

    Template parse(String text);

    Template parse(Expressions expressions);

    TemplateItem parse(Expression expression);

}

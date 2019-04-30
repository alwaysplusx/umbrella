package com.harmony.umbrella.template.spel;

import com.harmony.umbrella.template.Expression;
import com.harmony.umbrella.template.Template;
import com.harmony.umbrella.template.TemplateResolver;
import com.harmony.umbrella.template.support.SimpleTextTemplate;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * @author wuxii
 */
public class SpelTemplateResolver implements TemplateResolver {

    private ExpressionParser expressionParser = new SpelExpressionParser();

    @Override
    public <T extends Expression> Template<T> resolve(T expression) {
        return expression.isPlainText()
                ? new SimpleTextTemplate<>(expression)
                : new SpelTemplate<>(expression, expressionParser.parseExpression(expression.getExpression()));
    }

    public void setExpressionParser(ExpressionParser expressionParser) {
        this.expressionParser = expressionParser;
    }

}

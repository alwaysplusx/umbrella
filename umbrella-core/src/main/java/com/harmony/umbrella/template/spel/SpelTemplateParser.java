package com.harmony.umbrella.template.spel;

import com.harmony.umbrella.template.*;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuxii
 */
public class SpelTemplateParser extends AbstractTemplateParser {

    private ExpressionParser expressionParser = new SpelExpressionParser();

    @Override
    public Template parse(Expressions expressions) {
        List<TemplateItem> items = new ArrayList<>();
        for (Expression exp : expressions) {
            items.add(parse(exp));
        }
        return new SpelTemplate(expressions, items);
    }

    @Override
    public TemplateItem parse(Expression exp) {
        return exp.isPlainText()
                ? new SimpleTextTemplateItem(exp)
                : new SpelTemplateItem(exp, expressionParser.parseExpression(exp.getText()));
    }

    public void setExpressionParser(ExpressionParser expressionParser) {
        this.expressionParser = expressionParser;
    }

}
